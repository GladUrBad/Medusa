package com.gladurbad.api.listener;

import com.gladurbad.api.check.MedusaCheck;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MedusaSendAlertEvent extends Event implements Cancellable {
    private boolean cancelled;
    private final Player player;
    private final TextComponent message;
    private final MedusaCheck check;
    private final String info;

    public MedusaSendAlertEvent(final TextComponent message, final Player player, final MedusaCheck check, final String info) {
        super(true);
        this.player = player;
        this.message = message;
        this.check = check;
        this.info = info;
        this.cancelled = false;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public TextComponent getMessage() {
        return message;
    }

    public MedusaCheck getCheck() {
        return check;
    }

    public String getInfo() {
        return info;
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
