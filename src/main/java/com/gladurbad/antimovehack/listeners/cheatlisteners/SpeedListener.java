package com.gladurbad.antimovehack.listeners.cheatlisteners;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpeedListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = AntiMoveHack.getPlayerData(player);

        final double deltaXZ = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());

        final double EPSILON = 0.027;
        final double prediction = playerData.speedLastDeltaXZ * 0.91F;
        final double difference = Math.abs(prediction - deltaXZ);

        if (difference > EPSILON && !CollisionUtils.isOnGround(player)) {
            playerData.speedThreshold= Math.min(100, playerData.speedThreshold + 1);
            if (playerData.speedThreshold > 15) {
                AlertUtils.handleViolation(playerData, "Speed (A)", playerData.speedViolationLevel++, playerData.speedLastLegitLocation);
                player.teleport(playerData.speedLastLegitLocation);
                playerData.speedThreshold = 0;
            }
        } else {
            playerData.speedThreshold = Math.max(playerData.speedThreshold - 1, 0);
            playerData.speedLastLegitLocation = player.getLocation();
        }

        playerData.speedLastDeltaXZ = deltaXZ;
    }
}
