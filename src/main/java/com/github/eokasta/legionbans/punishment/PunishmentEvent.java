package com.github.eokasta.legionbans.punishment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = true)
@Data
public class PunishmentEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Punishment punishment;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
