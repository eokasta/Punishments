package com.github.eokasta.legionbans.commands;

import com.github.aldevteam.core.commands.annotations.CommandInformation;
import com.github.aldevteam.core.commands.exceptions.CommandLibException;
import com.github.aldevteam.core.commands.providers.Command;
import com.github.aldevteam.core.utils.Replacer;
import com.github.eokasta.legionbans.LegionBansPlugin;
import com.github.eokasta.legionbans.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandInformation(name = {"kick", "expulsar"},
        permission = "legionbans.kick",
        usage = "&f/kick <player> [reason] &7- &cPara expulsar um jogador."
)
public class KickCommand extends Command {

    private final LegionBansPlugin plugin;

    public KickCommand(LegionBansPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        plugin.registerCommands(this);
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {
        if (args.length == 0)
            throw new CommandLibException(getUsage());

        final Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null)
            throw new CommandLibException("&cJogador não encontrado.");

        final String reason = args.length == 1 ? "Sem motivo" : String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        plugin.getPunishmentManager().applyPunishment(target.getName(), PunishmentType.KICK, reason, sender, -1);
    }

}
