package com.github.eokasta.legionbans.punishment;

import com.github.eokasta.legionbans.LegionBansPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class PunishmentChannel extends JedisPubSub {

    private final LegionBansPlugin plugin;

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equals("punishment"))
            return;

        if (message.contains("apply-punishment:")) {
            final Punishment punishment = plugin.getGson().fromJson(message.split("apply-punishment:")[1], Punishment.class);
            Bukkit.getPluginManager().callEvent(new PunishmentEvent(punishment));
            return;
        }

        if (message.contains("unban-punishment:")) {
            final String[] split = message.split("unban-punishment:")[1].split("-");
            Bukkit.getPluginManager().callEvent(new UnbanPunishmentEvent(split[0], split[1]));
            return;
        }

        if (message.contains("unmute-punishment:")) {
            final String[] split = message.split("unmute-punishment:")[1].split("-");
            Bukkit.getPluginManager().callEvent(new UnmutePunishmentEvent(split[0], split[1]));
            return;
        }
    }

}
