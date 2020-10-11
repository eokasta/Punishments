package com.github.eokasta.legionbans.commands;

import com.github.aldevteam.core.commands.annotations.CommandInformation;
import com.github.aldevteam.core.commands.exceptions.CommandLibException;
import com.github.aldevteam.core.commands.providers.Command;
import com.github.aldevteam.core.utils.inventorymenulib.menus.PaginatedGUI;
import com.github.eokasta.legionbans.LegionBansPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInformation(name = {"historic", "historico", "lbhistoric"},
        permission = "legionbans.historic",
        usage = "&f/historic <player> &7- &cVeja o histórico de punição.",
        onlyPlayer = true
)
public class HistoricCommand extends Command {

    private final LegionBansPlugin plugin;

    public HistoricCommand(LegionBansPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        plugin.registerCommands(this);
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {
        final Player player = (Player) sender;
        if (args.length == 0)
            throw new CommandLibException(getUsage());

        final String target = args[0];
        final PaginatedGUI gui = plugin.getPunishmentManager().getPunishmentHistoric(target);
        if (gui == null)
            throw new CommandLibException("&cEsse jogador não possui histórico de punição.");

        gui.show(player);

    }
}
