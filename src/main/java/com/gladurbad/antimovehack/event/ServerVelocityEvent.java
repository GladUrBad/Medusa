package com.gladurbad.antimovehack.event;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;


public class ServerVelocityEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean async = false;
    private final Vector velocity;

    private final Player player;

    public ServerVelocityEvent(final Player player, final boolean async, final Vector velocity) {
        this.player = player;
        this.async = async;
        this.velocity = velocity;
    }


    public final Player getPlayer() {
        return player;
    }

    public boolean isAsync() {
        return async;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
