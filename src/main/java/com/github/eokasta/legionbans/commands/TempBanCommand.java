package com.github.eokasta.legionbans.commands;

import com.github.aldevteam.core.commands.annotations.CommandInformation;
import com.github.aldevteam.core.commands.exceptions.CommandLibException;
import com.github.aldevteam.core.commands.providers.Command;
import com.github.aldevteam.core.utils.DataUtils;
import com.github.eokasta.legionbans.LegionBansPlugin;
import com.github.eokasta.legionbans.punishment.PunishmentType;
import com.github.eokasta.legionbans.utils.Verifications;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Date;

@CommandInformation(name = "tempban",
        permission = "legionbans.tempban",
        usage = "&f/tempban <player> <time> <s|m|h|d> [reason] &7- &cPara banir temporariamente um jogador."
)
public class TempBanCommand extends Command {

    private final LegionBansPlugin plugin;

    public TempBanCommand(LegionBansPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        plugin.registerCommands(this);
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {
        if (args.length < 3)
            throw new CommandLibException(getUsage());

        final String player = args[0];
        final Integer time = Verifications.getInt(args[1]);
        if (time == null || time <= 0)
            throw new CommandLibException(getUsage());

        final String timeType = args[2].toLowerCase();

        Date date;
        switch (timeType) {
            case "s":
                date = DataUtils.sumTimeToDate(new Date(), 0, 0, time + 1);
                break;
            case "m":
                date = DataUtils.sumTimeToDate(new Date(), 0, time, 1);
                break;
            case "h":
                date = DataUtils.sumTimeToDate(new Date(), time, 0, 1);
                break;
            case "d":
                date = DataUtils.sumTimeToDate(new Date(), time * 24, 0, 1);
                break;
            default:
                throw new CommandLibException(getUsage());
        }

        final String reason = args.length == 3 ? "Sem motivo" : String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        plugin.getPunishmentManager().applyPunishment(player, PunishmentType.TEMPORARY_BAN, reason, sender, date.getTime());
    }
}
