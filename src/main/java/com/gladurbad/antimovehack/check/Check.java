package com.gladurbad.antimovehack.check;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.manager.AlertManager;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class Check implements Listener {

    protected final PlayerData data;
    @Getter
    private int vl;
    protected double buffer;
    @Getter
    @Setter
    protected Location lastLegitLocation;

    public Check(PlayerData data) {
        this.data = data;
        Bukkit.getServer().getPluginManager().registerEvents(this, AntiMoveHack.getAntiMoveHack());
    }

    public abstract void handle(final Packet packet);

    public CheckInfo getCheckInfo() {
        return this.getClass().getAnnotation(CheckInfo.class);
    }

    protected void fail() {
        ++vl;
        AlertManager.verbose(data, this);
    }

    protected void failAndSetback() {
        ++vl;
        AlertManager.verbose(data, this);
        data.getPlayer().teleport(lastLegitLocation);
        data.setLastSetbackTime(System.currentTimeMillis());
        buffer = 0;
    }

    protected void increaseBuffer() {
        buffer = Math.min(100, buffer + 1);
    }

    protected void decreaseBuffer() {
        buffer = Math.max(0, buffer - 1);
    }

    protected boolean isFlyingPacket(Packet packet) {
        return PacketType.Client.Util.isInstanceOfFlying(packet.getPacketId());
    }


    protected void debug(String info){ Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(double info){ Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(long info){ Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(boolean info){ Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debugPerPlayer(String info) { data.getPlayer().sendMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info);}

}
