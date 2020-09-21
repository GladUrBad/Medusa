package com.gladurbad.medusa.check;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.api.check.MedusaCheck;
import com.gladurbad.api.listener.MedusaAlertEvent;
import com.gladurbad.medusa.config.*;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.manager.AlertManager;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;


@Getter
@Setter
public abstract class Check implements MedusaCheck {

    //Data for check.
    protected final PlayerData data;

    //Check data from config.
    private final boolean enabled;
    private final int maxVl;
    private final boolean setback;
    private final String punishCommand;

    //Check information.
    private int vl;
    protected double buffer;
    protected Location lastLegitLocation;

    public Check(PlayerData data) {
        this.data = data;
        this.enabled = Config.ENABLED_CHECKS.contains(getCheckInfo().name() + getCheckInfo().type());
        this.maxVl = Config.MAX_VIOLATIONS.get(getCheckInfo().name() + getCheckInfo().type());
        this.setback = Config.SETBACK_CHECKS.contains(getCheckInfo().name() + getCheckInfo().type());
        this.punishCommand = Config.PUNISH_COMMANDS.get(getCheckInfo().name() + getCheckInfo().type());
    }

    public abstract void handle(final Packet packet);

    public CheckInfo getCheckInfo() {
        return this.getClass().getAnnotation(CheckInfo.class);
    }

    protected void fail() {
        ++vl;

        MedusaAlertEvent event = new MedusaAlertEvent(this, setback);
        Bukkit.getPluginManager().callEvent(event);

        if(!event.isCancelled()) {
            if (vl >= Config.VL_TO_ALERT) {
                AlertManager.verbose(data, this);
                if (event.isSetback()) {
                    final Location setBackLocation = lastLegitLocation == null ? data.getLastBukkitLocation() : lastLegitLocation;
                    Bukkit.getScheduler().runTask(Medusa.getInstance(), () -> data.getPlayer().teleport(setBackLocation));
                    data.setLastSetbackTime(now());
                    buffer = 0;
                }
            }
        }
    }

    protected double increaseBuffer() {
        buffer = Math.min(10000, buffer + 1);
        return buffer;
    }

    protected double decreaseBuffer() {
        buffer = Math.max(0, buffer - 1);
        return buffer;
    }

    protected double increaseBufferBy(double amount) {
        buffer = Math.min(100, buffer + amount);
        return buffer;
    }

    protected double decreaseBufferBy(double amount) {
        buffer = Math.max(0, buffer - amount);
        return buffer;
    }

    protected void resetBuffer() {
        buffer = 0;
    }

    protected void multiplyBuffer(double multiplier) {
        buffer *= multiplier;
    }

    protected long now() {
        return System.currentTimeMillis();
    }


    protected void debug(Object info) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info);
    }

    protected void debugPerPlayer(Object info) { data.getPlayer().sendMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info);}

}
