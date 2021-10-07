package com.gladurbad.api.listener;

import com.gladurbad.api.check.MedusaCheck;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MedusaFlagEvent extends Event implements Cancellable {

    private boolean cancelled;

    private final Player player;
    private final MedusaCheck check;


    public MedusaFlagEvent(Player player, MedusaCheck check) {
        super(true);
        this.player = player;
        this.check = check;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public MedusaCheck getCheck() {
        return check;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        cancelled = b;
    }
}