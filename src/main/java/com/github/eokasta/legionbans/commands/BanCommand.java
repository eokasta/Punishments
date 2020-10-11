package com.github.eokasta.legionbans.commands;

import com.github.aldevteam.core.commands.annotations.CommandInformation;
import com.github.aldevteam.core.commands.exceptions.CommandLibException;
import com.github.aldevteam.core.commands.providers.Command;
import com.github.eokasta.legionbans.LegionBansPlugin;
import com.github.eokasta.legionbans.punishment.PunishmentType;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@CommandInformation(name = {"ban", "banir"},
        permission = "legionbans.ban",
        usage = "&f/ban <player> [reason] &7- &cPara banir um jogador."
)
public class BanCommand extends Command {

    private final LegionBansPlugin plugin;

    public BanCommand(LegionBansPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        plugin.registerCommands(this);
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {
        if (args.length == 0)
            throw new CommandLibException(getUsage());

        final String reason = args.length == 1 ? "Sem motivo" : String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        plugin.getPunishmentManager().applyPunishment(args[0], PunishmentType.PERMANENT_BAN, reason, sender, -1);
    }
}
