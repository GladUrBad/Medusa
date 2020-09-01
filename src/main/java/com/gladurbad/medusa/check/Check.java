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

public abstract class Check implements Listener {

    protected final PlayerData data;
    @Getter
    private int vl;
    protected double buffer;
    @Getter
    @Setter
    protected Location lastLegitLocation;

    //Check data from config.
    @Getter
    private boolean enabled;
    @Getter
    private int maxVL;
    @Getter
    private boolean setback;
    @Getter
    private double vlAdd;
    @Getter
    private int vlDecay;
    @Getter
    private int vlDelay;
//    @Getter
//    private String punishCommand;

    public Check(PlayerData data) {
        this.data = data;
        this.enabled = Config.ENABLED_CHECKS.contains(getCheckInfo().name() + getCheckInfo().type());
        this.maxVL = Config.MAX_VIOLATIONS.get(getCheckInfo().name() + getCheckInfo().type());
        this.setback = Config.SETBACK_CHECKS.contains(getCheckInfo().name() + getCheckInfo().type());
        this.vlAdd = Config.VL_ADD.get(getCheckInfo().name() + getCheckInfo().type());
        this.vlDecay = Config.VL_DECAY.get(getCheckInfo().name() + getCheckInfo().type());
        this.vlDelay = Config.VL_DELAY.get(getCheckInfo().name() + getCheckInfo().type());
        if (this.vlDecay <= 0)
            this.vlDecay = 1;
        //this.punishCommand = Config.PUNISH_COMMANDS.get(getCheckInfo().name() + getCheckInfo().type());
        Bukkit.getServer().getPluginManager().registerEvents(this, Medusa.getInstance());
    }

    public abstract void handle(final Packet packet);

    public CheckInfo getCheckInfo() {
        return this.getClass().getAnnotation(CheckInfo.class);
    }

    private long lastFail;
    private boolean failed;

    public int getVl() {
        if (failed) {
            failed = false;
            lastFail = now();
            return vl = (int) Math.min(vl, Math.max(0, vl - (((now() - lastFail) / 50) - vlDelay) / vlDecay));
        }
        lastFail = now();
        return vl = (int) Math.min(vl, Math.max(0, vl - (((now() - lastFail) / 50)) / vlDecay));
    }

    protected void fail() {
        if (getVl() < maxVL)
            ++vl;
        AlertManager.verbose(data, this);
        if(setback && vl > Config.VL_TO_ALERT) {
            final Location setBackLocation = lastLegitLocation == null ? data.getLastLocation() : lastLegitLocation;
            Bukkit.getScheduler().runTask(Medusa.getInstance(), () -> data.getPlayer().teleport(setBackLocation));
            data.setLastSetbackTime(now());
            buffer = 0;
        }
        lastFail = now();
        failed = true;
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

    protected long now() {
        return System.currentTimeMillis();
    }


    protected void debug(String info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(double info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(long info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debug(boolean info) { Bukkit.broadcastMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info); }
    protected void debugPerPlayer(String info) { data.getPlayer().sendMessage(ChatColor.AQUA + "Debug: " + ChatColor.RESET + info);}

}
