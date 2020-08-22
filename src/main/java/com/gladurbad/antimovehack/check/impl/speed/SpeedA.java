package com.gladurbad.antimovehack.check.impl.speed;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final double deltaXZ = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());

        final double EPSILON = 0.027;
        final double prediction = data.speedLastDeltaXZ * 0.91F;
        final double difference = Math.abs(prediction - deltaXZ);

        if (difference > EPSILON && !CollisionUtils.isOnGround(player)) {
            data.speedThreshold= Math.min(100, data.speedThreshold + 1);
            if (data.speedThreshold > 5) {
                AlertUtils.handleViolation(data, "Speed (A)", data.speedViolationLevel++, data.speedLastLegitLocation);
                player.teleport(data.speedLastLegitLocation);
                data.speedThreshold = 0;
            }
        } else {
            data.speedThreshold = Math.max(data.speedThreshold - 1, 0);
            data.speedLastLegitLocation = player.getLocation();
        }

        data.speedLastDeltaXZ = deltaXZ;
    }
}
