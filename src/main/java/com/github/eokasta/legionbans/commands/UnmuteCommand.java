package com.github.eokasta.legionbans.commands;

import com.github.aldevteam.core.commands.annotations.CommandInformation;
import com.github.aldevteam.core.commands.exceptions.CommandLibException;
import com.github.aldevteam.core.commands.providers.Command;
import com.github.eokasta.legionbans.LegionBansPlugin;
import com.github.eokasta.legionbans.punishment.Punishment;
import org.bukkit.command.CommandSender;

@CommandInformation(name = "unmute",
        permission = "legionbans.unmute",
        usage = "&f/unmute <player> &7- &cDessilencie um jogador."
)
public class UnmuteCommand extends Command {

    private final LegionBansPlugin plugin;

    public UnmuteCommand(LegionBansPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        plugin.registerCommands(this);
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {
        if (args.length == 0)
            throw new CommandLibException(getUsage());

        final Punishment punishment = plugin.getPunishmentManager().getActiveMute(args[0]);
        if (punishment == null)
            throw new CommandLibException("&cEste jogador não está silenciado.");

        plugin.getPunishmentManager().unmutePunishment(punishment.getPlayer(), sender);
    }
}
