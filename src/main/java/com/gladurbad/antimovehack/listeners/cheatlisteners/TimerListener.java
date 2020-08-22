package com.gladurbad.antimovehack.listeners.cheatlisteners;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.event.FlyingEvent;
import com.gladurbad.antimovehack.event.ServerTeleportEvent;
import com.gladurbad.antimovehack.playerdata.PlayerData;

import com.gladurbad.antimovehack.util.AlertUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TimerListener implements Listener {

    @EventHandler
    public void onFlying(FlyingEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = AntiMoveHack.getPlayerData(player);

        if(++playerData.timerTeleportTicks > 20) {
            final long time = System.currentTimeMillis();
            final long delay = time - playerData.timerLastTime;

            playerData.timerDifferences.add(delay);
            if (playerData.timerDifferences.size() >= 20) {
                double timerAverage = playerData.timerDifferences.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
                double timerSpeed = 50 / timerAverage;
                if (Math.abs(timerSpeed - 1.0D) >= 0.1) {
                    AlertUtils.handleViolation(playerData, "Timer (A)", playerData.timerViolationLevel++, null);
                }
                playerData.timerDifferences.clear();
            }
            playerData.timerLastTime = time;
        }
    }

    @EventHandler
    public void onTeleport(ServerTeleportEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = AntiMoveHack.getPlayerData(player);
        playerData.timerTeleportTicks = 0;
    }
}
