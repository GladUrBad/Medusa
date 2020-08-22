package com.gladurbad.antimovehack.playerdata;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.manager.CheckManager;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.UUID;


public class PlayerData implements Listener {

    @Getter
    private final Player player;
    @Getter
    private final UUID playerUUID;
    @Getter
    private List<Check> checks;

    public PlayerData(UUID playerUUID, Player player) {
        this.playerUUID = playerUUID;
        this.player = player;
        this.checks = CheckManager.loadChecks(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, AntiMoveHack.getAntiMoveHack());
    }

    //Movement data.
    public double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    public Location lastLocation, location;

    //Teleportation & setback data.
    public long lastSetbackTime;

    @EventHandler
    public void handle(PlayerMoveEvent event) {
        if(event.getPlayer().getEntityId() == this.getPlayer().getEntityId()) {
            this.lastLocation = event.getFrom();
            this.location = event.getTo();

            double lastDeltaX = deltaX;
            double deltaX = location.getX() - lastLocation.getX();

            this.lastDeltaX = lastDeltaX;
            this.deltaX = deltaX;

            double lastDeltaY = deltaY;
            double deltaY = location.getY() - lastLocation.getY();

            this.lastDeltaY = lastDeltaY;
            this.deltaY = deltaY;

            double lastDeltaZ = deltaZ;
            double deltaZ = location.getZ() - lastLocation.getZ();

            this.lastDeltaZ = lastDeltaZ;
            this.deltaZ = deltaZ;

            double lastDeltaXZ = deltaXZ;
            double deltaXZ = location.clone().toVector().setY(0.0).distance(lastLocation.clone().toVector().setY(0.0));

            this.lastDeltaXZ = lastDeltaXZ;
            this.deltaXZ = deltaXZ;

            checks.forEach(check -> check.handle(event));



        }
    }


}
