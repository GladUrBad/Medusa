package com.gladurbad.antimovehack.check.impl.fastclimb;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;


import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class FastClimbA extends Check {

    public FastClimbA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final double deltaY = event.getTo().getY() - event.getFrom().getY();

        if(CollisionUtils.isCollidingWithClimbable(data.getPlayer()) && deltaY > 0.1177) {
            data.fastClimbThreshold = Math.min(100, data.fastClimbThreshold + 1);
            if (data.fastClimbThreshold > 5) {
                AlertUtils.handleViolation(data, "FastClimb (A)", data.fastClimbViolationLevel++, data.fastClimbLastLegitLocation);
                data.getPlayer().teleport(data.fastClimbLastLegitLocation);
                data.fastClimbThreshold = 0;
            }
        } else {
            data.fastClimbThreshold = Math.max(data.fastClimbThreshold - 1, 0);
            data.fastClimbLastLegitLocation = data.getPlayer().getLocation();
        }



    }
}
