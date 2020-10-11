package com.github.eokasta.legionbans.listeners;

import com.github.aldevteam.core.utils.Helper;
import com.github.aldevteam.core.utils.Replacer;
import com.github.eokasta.legionbans.LegionBansPlugin;
import com.github.eokasta.legionbans.punishment.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PunishmentListener implements Listener {

    private final LegionBansPlugin plugin;

    public PunishmentListener(LegionBansPlugin plugin) {
        this.plugin = plugin;

        plugin.registerListener(this);
    }

    @EventHandler
    public void onTemporaryMute(PunishmentEvent event) {
        final Punishment punishment = event.getPunishment();
        if (punishment.getType() != PunishmentType.TEMPORARY_MUTE)
            return;

        final Replacer replacer = new Replacer();
        replacer.add("%reason%", punishment.getReason());
        replacer.add("%author%", punishment.getAuthor());
        replacer.add("%player%", punishment.getPlayer());
        replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));
        replacer.add("%expiresAt%", Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()));

        for (String s : plugin.getSettings().listReplaceOf("tempmute-message", replacer))
            Bukkit.broadcastMessage(s);
    }

    @EventHandler(ignoreCancelled = true)
    public void tryChatWhenMuted(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Punishment punishment = plugin.getPunishmentManager().getActiveMute(player.getName());
        if (punishment != null) {
            event.setCancelled(true);

            final Replacer replacer = new Replacer();
            replacer.add("%reason%", punishment.getReason());
            replacer.add("%author%", punishment.getAuthor());
            replacer.add("%player%", punishment.getPlayer());
            replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));
            replacer.add("%expiresAt%", Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()));

            player.sendMessage(String.join("\n ", plugin.getSettings().listReplaceOf("tempmute-player-message", replacer)));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void tryCommandWhenMuted(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final Punishment punishment = plugin.getPunishmentManager().getActiveMute(player.getName());
        if (punishment == null)
            return;

        if (!plugin.getSettings().getMuteBlockedCommands().contains(event.getMessage().split(" ")[0].toLowerCase().replaceFirst("/", "")))
            return;

        event.setCancelled(true);

        final Replacer replacer = new Replacer();
        replacer.add("%reason%", punishment.getReason());
        replacer.add("%author%", punishment.getAuthor());
        replacer.add("%player%", punishment.getPlayer());
        replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));
        replacer.add("%expiresAt%", Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()));

        player.sendMessage(Helper.format(String.join("\n ", plugin.getSettings().listReplaceOf("tempmute-player-message", replacer))));
    }

    @EventHandler
    public void onKick(PunishmentEvent event) {
        final Punishment punishment = event.getPunishment();
        if (punishment.getType() != PunishmentType.KICK)
            return;

        final Replacer replacer = new Replacer();
        replacer.add("%reason%", punishment.getReason());
        replacer.add("%author%", punishment.getAuthor());
        replacer.add("%player%", punishment.getPlayer());
        replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));

        final Player target = Bukkit.getPlayerExact(punishment.getPlayer());
        if (target == null)
            return;

        Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer(String.join("\n ", plugin.getSettings().listReplaceOf("kick-player-message", replacer))));

        for (String s : plugin.getSettings().listReplaceOf("kick-message", replacer))
            Bukkit.broadcastMessage(s);
    }

    @EventHandler
    public void onPermanentBan(PunishmentEvent event) {
        final Punishment punishment = event.getPunishment();
        if (punishment.getType() != PunishmentType.PERMANENT_BAN)
            return;

        final Replacer replacer = new Replacer();
        replacer.add("%reason%", punishment.getReason());
        replacer.add("%author%", punishment.getAuthor());
        replacer.add("%player%", punishment.getPlayer());
        replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));

        for (String s : plugin.getSettings().listReplaceOf("ban-message", replacer))
            Bukkit.broadcastMessage(s);

        final Player target = Bukkit.getPlayerExact(punishment.getPlayer());
        if (target != null)
            Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer(String.join("\n ", plugin.getSettings().listReplaceOf("ban-player-message", replacer))));
    }

    @EventHandler
    public void onTemporaryBan(PunishmentEvent event) {
        final Punishment punishment = event.getPunishment();
        if (punishment.getType() != PunishmentType.TEMPORARY_BAN)
            return;

        final Replacer replacer = new Replacer();
        replacer.add("%reason%", punishment.getReason());
        replacer.add("%author%", punishment.getAuthor());
        replacer.add("%player%", punishment.getPlayer());
        replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));
        replacer.add("%expiresAt%", Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()));

        for (String s : plugin.getSettings().listReplaceOf("tempban-message", replacer))
            Bukkit.broadcastMessage(s);

        final Player target = Bukkit.getPlayerExact(punishment.getPlayer());
        if (target != null)
            Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer(String.join("\n ", plugin.getSettings().listReplaceOf("tempban-player-message", replacer))));
    }

    @EventHandler
    public void onUnbanPunishment(UnbanPunishmentEvent event) {
        final Replacer replacer = new Replacer();
        replacer.add("%player%", event.getPlayer());
        replacer.add("%author%", event.getAuthor());

        for (String s : plugin.getSettings().listReplaceOf("unban-message", replacer))
            Bukkit.broadcastMessage(Helper.format(s));
    }

    @EventHandler
    public void onUnmutePunishment(UnmutePunishmentEvent event) {
        final Replacer replacer = new Replacer();
        replacer.add("%player%", event.getPlayer());
        replacer.add("%author%", event.getAuthor());

        for (String s : plugin.getSettings().listReplaceOf("unmute-message", replacer))
            Bukkit.broadcastMessage(Helper.format(s));
    }


    @EventHandler
    public void tryJoinWhenBanned(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final Punishment punishment = plugin.getPunishmentManager().getActiveBan(player.getName());
        if (punishment != null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);

            final Replacer replacer = new Replacer();
            replacer.add("%reason%", punishment.getReason());
            replacer.add("%author%", punishment.getAuthor());
            replacer.add("%player%", punishment.getPlayer());
            replacer.add("%appliedAt%", LegionBansPlugin.FORMATTER.format(punishment.getAppliedAt()));
            replacer.add("%expiresAt%", Helper.DateRemainingFormat(System.currentTimeMillis(), punishment.getExpiresAt()));

            event.setKickMessage(String.join("\n ", punishment.getType() == PunishmentType.PERMANENT_BAN
                    ? plugin.getSettings().listReplaceOf("ban-player-message", replacer)
                    : plugin.getSettings().listReplaceOf("tempban-player-message", replacer)));
        }
    }

}
