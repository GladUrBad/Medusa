package com.gladurbad.antimovehack.check.impl.flight;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;


public class FlightA extends Check {

    public FlightA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        final double deltaY = event.getTo().getY() - event.getFrom().getY();

        final double prediction = (data.flightLastDeltaY - 0.08D) * 0.98D;
        final double difference = Math.abs(deltaY - prediction);

        final double EPSILON = 0.05;

        if (difference > EPSILON && !CollisionUtils.isOnGround(data.getPlayer()) && !CollisionUtils.isNearBoat(data.getPlayer())) {
            data.flightThreshold = Math.min(100, data.flightThreshold + 1);
            if (data.flightThreshold > 5) {
                AlertUtils.handleViolation(data, "Flight (A)", data.flightViolationLevel++, data.flightLastLegitLocation);
                data.getPlayer().teleport(data.flightLastLegitLocation);
                data.flightThreshold = 0;
            }
        } else {
            data.flightThreshold = Math.max(data.flightThreshold - 1, 0);
            data.flightLastLegitLocation = data.getPlayer().getLocation();
        }

        data.flightLastDeltaY = deltaY;
    }

}
