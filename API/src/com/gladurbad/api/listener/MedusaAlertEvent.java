package com.gladurbad.api.listener;

import com.gladurbad.api.check.MedusaCheck;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class MedusaAlertEvent extends Event implements Cancellable {

    private boolean cancelled, setback;

    private final Player player;
    private final MedusaCheck check;

    public MedusaAlertEvent(Player player, MedusaCheck check, boolean setback) {
        super(true);
        this.player = player;
        this.check = check;
        this.setback = setback;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
