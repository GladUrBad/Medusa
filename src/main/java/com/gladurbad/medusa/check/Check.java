package com.gladurbad.medusa.check;

import com.gladurbad.medusa.config.*;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.manager.AlertManager;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;


@Getter
@Setter
public abstract class Check {

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

        if (vl >= Config.VL_TO_ALERT) {
            AlertManager.verbose(data, this);
            if (setback) {
                final Location setBackLocation = lastLegitLocation == null ? data.getLastLocation() : lastLegitLocation;
                Bukkit.getScheduler().runTask(Medusa.getInstance(), () -> data.getPlayer().teleport(setBackLocation));
                data.setLastSetbackTime(now());
                buffer = 0;
            }
        }
    }

    protected void increaseBuffer() {
        buffer = Math.min(100, buffer + 1);
    }

    protected void decreaseBuffer() {
        buffer = Math.max(0, buffer - 1);
    }

    protected void increaseBufferBy(double amount) {
        buffer = Math.min(100, buffer + amount);
    }

    protected void decreaseBufferBy(double amount) {
        buffer = Math.max(0, buffer - amount);
    }

    protected void multiplyBuffer(double multiplier) {
        buffer *= multiplier;
    }

    protected boolean isFlyingPacket(Packet packet) {
        return PacketType.Client.Util.isInstanceOfFlying(packet.getPacketId());
    }

    protected boolean isRotationPacket(Packet packet) {
        return packet.getPacketId() == PacketType.Client.LOOK || packet.getPacketId() == PacketType.Client.POSITION_LOOK;
    }

    protected long now() {
        return System.currentTimeMillis();
    }


    protected void debug(String info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(double info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(long info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(boolean info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debugPerPlayer(String info) { data.getPlayer().sendMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info);}

}
