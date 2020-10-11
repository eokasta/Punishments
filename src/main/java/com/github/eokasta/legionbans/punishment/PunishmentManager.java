package com.github.eokasta.legionbans.punishment;

import com.github.aldevteam.core.storage.redis.JedisManager;
import com.github.aldevteam.core.utils.Helper;
import com.github.aldevteam.core.utils.MakeItem;
import com.github.aldevteam.core.utils.inventorymenulib.PaginatedGUIBuilder;
import com.github.aldevteam.core.utils.inventorymenulib.buttons.ItemButton;
import com.github.aldevteam.core.utils.inventorymenulib.menus.PaginatedGUI;
import com.github.eokasta.legionbans.LegionBansPlugin;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Data
public class PunishmentManager {

    private final LegionBansPlugin plugin;
    private final String host;
    private final int port;

    private JedisManager jedisManager;

    public void init() {
        this.jedisManager = new JedisManager(plugin, host, port);
        jedisManager.registerChannel(new PunishmentChannel(plugin), "punishment");
    }

    public void applyPunishment(String player,
                                    PunishmentType type,
                                    String reason,
                                    CommandSender author,
                                    long expiresAt)
    {

        if (type == PunishmentType.PERMANENT_BAN || type == PunishmentType.TEMPORARY_BAN) {
            final Punishment activeBan = getActiveBan(player);
            if (activeBan != null) {
                activeBan.setActive(false);
                plugin.getDatabaseManager().updatePunishment(activeBan);
            }
        }

        if (type == PunishmentType.TEMPORARY_MUTE) {
            final Punishment activeMute = getActiveMute(player);
            System.out.println(plugin.getGson().toJson(activeMute));
            if (activeMute != null) {
                activeMute.setActive(false);
                plugin.getDatabaseManager().updatePunishment(activeMute);
            }
        }

        final Punishment punishment = new Punishment(player, type, reason, author.getName(), expiresAt, System.currentTimeMillis());
        punishment.setActive(true);
        jedisManager.sendMessageChannel("punishment", "apply-punishment:" + plugin.getGson().toJson(punishment));
        plugin.getDatabaseManager().insertPunishment(punishment);
    }

    public void unbanPunishment(String player, CommandSender sender) {
        final Punishment punishment = plugin.getPunishmentManager().getActiveBan(player);
        if (punishment == null)
            return;

        punishment.setActive(false);
        plugin.getDatabaseManager().updatePunishment(punishment);
        jedisManager.sendMessageChannel("punishment", "unban-punishment:" + player + "-" + sender.getName());
    }

    public void unmutePunishment(String player, CommandSender sender) {
        final Punishment punishment = plugin.getPunishmentManager().getActiveMute(player);
        if (punishment == null)
            return;

        punishment.setActive(false);
        jedisManager.sendMessageChannel("punishment", "unmute-punishment:" + player + "-" + sender.getName());
        Helper.executeAsync(() -> plugin.getDatabaseManager().updatePunishment(punishment));
    }

    public List<Punishment> getPunishments(String player) {
        final List<Punishment> punishments = plugin.getDatabaseManager().getPunishments(player);

        for (Punishment punishment : punishments) {
            if (punishment.isActive() && punishment.getExpiresAt() != -1 && punishment.getExpiresAt() < System.currentTimeMillis()) {
                punishment.setActive(false);
                Helper.executeAsync(() -> plugin.getDatabaseManager().updatePunishment(punishment));
            }
        }

        return punishments;
    }

    public Punishment getActiveMute(String player) {
        final List<Punishment> punishments = getPunishments(player);

        Punishment finalPunishment = null;

        for (Punishment punishment : punishments)
            if (punishment.getType() == PunishmentType.TEMPORARY_MUTE && punishment.isActive())
                finalPunishment = punishment;

        return finalPunishment;
    }

    public Punishment getActiveBan(String player) {
        final List<Punishment> punishments = getPunishments(player);

        Punishment finalPunishment = null;

        for (Punishment punishment : punishments)
            if ((punishment.getType() == PunishmentType.TEMPORARY_BAN
                    || punishment.getType() == PunishmentType.PERMANENT_BAN)
                    && punishment.isActive())
                finalPunishment = punishment;

        return finalPunishment;
    }

    public PaginatedGUI getPunishmentHistoric(String player) {
        final List<Punishment> punishments = plugin.getPunishmentManager().getPunishments(player);
        if (punishments.isEmpty())
            return null;

        final PaginatedGUIBuilder guiBuilder = new PaginatedGUIBuilder("Histórico: " + player, "xxxxxxxxxx#######x<#######>x#######x");
        guiBuilder.setDefaultAllCancell(true);

        guiBuilder.setNextPageItem(Material.ARROW, 1, Helper.format("&aPróxima"));
        guiBuilder.setPreviousPageItem(Material.ARROW, 1, Helper.format("&aAnterior"));

        final List<ItemButton> content = new ArrayList<>();

        for (Punishment punishment : punishments) {
            final MakeItem makeItem = new MakeItem(Material.APPLE);

            switch (punishment.getType()) {
                case KICK:
                    makeItem.setType(Material.REDSTONE);
                    makeItem.setName("&a#" + punishment.getId() + " - Expulso");
                    makeItem.addLoreList("",
                            " &7Aplicada em: &f" + LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()),
                            " &7Motivo: &f" + punishment.getReason(),
                            " &7Autor: &f" + punishment.getAuthor(),
                            ""
                    );
                    content.add(new ItemButton(makeItem.build()));
                    break;
                case PERMANENT_BAN:
                    makeItem.setType(Material.BEDROCK);
                    makeItem.setName("&a#" + punishment.getId() + " - Banido permanentemente");
                    makeItem.addLoreList("",
                            " &7Aplicada em: &f" + LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()),
                            " &7Motivo: &f" + punishment.getReason(),
                            " &7Autor: &f" + punishment.getAuthor(),
                            " &7Ativo: &f" + (punishment.isActive() ? "&aSim" : "&cNão"),
                            ""
                    );
                    content.add(new ItemButton(makeItem.build()));
                    break;
                case TEMPORARY_BAN:
                    makeItem.setType(Material.BARRIER);
                    makeItem.setName("&a#" + punishment.getId() + " - Banido temporariamente");
                    makeItem.addLoreList("",
                            " &7Aplicada em: &f" + LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()),
                            " &7Motivo: &f" + punishment.getReason(),
                            " &7Autor: &f" + punishment.getAuthor(),
                            " &7Expira em: &f" + (punishment.isActive() ? Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()) : "&aExpirado"),
                            ""
                    );
                    content.add(new ItemButton(makeItem.build()));
                    break;
                case TEMPORARY_MUTE:
                    makeItem.setType(Material.BOOK);
                    makeItem.setName("&a#" + punishment.getId() + " - Silenciado");
                    makeItem.addLoreList("",
                            " &7Aplicada em: &f" + LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()),
                            " &7Motivo: &f" + punishment.getReason(),
                            " &7Autor: &f" + punishment.getAuthor(),
                            " &7Expira em: &f" + (punishment.isActive() ? Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()) : "&aExpirado"),
                            ""
                    );
                    content.add(new ItemButton(makeItem.build()));
                    break;

            }

        }

        guiBuilder.setContent(content);

        return guiBuilder.build();
    }

}
