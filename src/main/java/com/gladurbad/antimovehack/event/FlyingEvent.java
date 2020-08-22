package com.gladurbad.antimovehack.event;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlyingEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean async;

    private Player player;

    public FlyingEvent(final Player player, final boolean async) {
        this.player = player;
        this.async = async;
    }


    public final Player getPlayer() {
        return player;
    }


    public boolean isAsync() {
        return async;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
