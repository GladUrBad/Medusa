package com.gladurbad.antimovehack.listeners.cheatlisteners;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class FlightListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = AntiMoveHack.getPlayerData(player);

        final double deltaY = event.getTo().getY() - event.getFrom().getY();

        final double prediction = (playerData.flightLastDeltaY - 0.08D) * 0.98D;
        final double difference = Math.abs(deltaY - prediction);

        final double EPSILON = 0.05;

        if (difference > EPSILON && !CollisionUtils.isOnGround(player) && !CollisionUtils.isNearBoat(player)) {
            playerData.flightThreshold = Math.min(100, playerData.flightThreshold + 1);
            if (playerData.flightThreshold > 15) {
                AlertUtils.handleViolation(playerData, "Flight (A)", playerData.flightViolationLevel++, playerData.flightLastLegitLocation);
                playerData.flightThreshold = 0;
            }
        } else {
            playerData.flightThreshold = Math.max(playerData.flightThreshold - 1, 0);
            playerData.flightLastLegitLocation = player.getLocation();
        }

        playerData.flightLastDeltaY = deltaY;
    }

}
