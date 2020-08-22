package com.gladurbad.antimovehack.listeners.cheatlisteners;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FastClimbListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = AntiMoveHack.getPlayerData(player);

        final double deltaY = event.getTo().getY() - event.getFrom().getY();

        if(CollisionUtils.isCollidingWithClimbable(player) && deltaY > 0.1177) {
            playerData.fastClimbThreshold = Math.min(100, playerData.fastClimbThreshold + 1);
            if (playerData.fastClimbThreshold > 15) {
                AlertUtils.handleViolation(playerData, "FastClimb (A)", playerData.fastClimbViolationLevel++, playerData.fastClimbLastLegitLocation);
                player.teleport(playerData.fastClimbLastLegitLocation);
                playerData.fastClimbThreshold = 0;
            }
        } else {
            playerData.fastClimbThreshold = Math.max(playerData.fastClimbThreshold - 1, 0);
            playerData.fastClimbLastLegitLocation = player.getLocation();
        }



    }
}
