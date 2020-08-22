package com.gladurbad.antimovehack.check.impl.jesus;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JesusA extends Check {

    public JesusA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        checkA(event, player, data);
        checkB(event, player, data);
    }

    public void checkA(PlayerMoveEvent event, Player player, PlayerData playerData) {
        final boolean invalid = CollisionUtils.isOnChosenBlock(player, -0.1, Material.WATER, Material.STATIONARY_WATER) &&
                CollisionUtils.isOnChosenBlock(player, 0.1, Material.AIR);

        if(invalid) {
            playerData.jesusThreshold = Math.min(200, playerData.jesusThreshold + 1);
            if(playerData.jesusThreshold > 5) {
                AlertUtils.handleViolation(playerData, "Jesus (A)", playerData.jesusViolationLevel++, playerData.jesusLastLegitLocation);
                playerData.jesusThreshold = 0;
            }
        } else {
            playerData.jesusThreshold = 0;
            playerData.jesusLastLegitLocation = player.getLocation();
        }
    }

    public void checkB(PlayerMoveEvent event, Player player, PlayerData playerData) {
        final double deltaY = event.getTo().getY() - event.getFrom().getY();

        final boolean invalid = CollisionUtils.isOnChosenBlock(player, 0.1, Material.WATER, Material.STATIONARY_WATER) &&
                                !CollisionUtils.isOnSolid(player) && deltaY == 0.0D;

        if(invalid) {
            playerData.jesusBThreshold = Math.min(200, playerData.jesusBThreshold + 1);
            if(playerData.jesusBThreshold > 5) {
                AlertUtils.handleViolation(playerData, "Jesus (B)", playerData.jesusBViolationLevel++, playerData.jesusBLastLegitLocation);
                playerData.jesusBThreshold = 0;
            }
        } else {
            playerData.jesusBThreshold = Math.max(playerData.jesusBThreshold - 1, 0);
            playerData.jesusBLastLegitLocation = player.getLocation();
        }
    }
}
