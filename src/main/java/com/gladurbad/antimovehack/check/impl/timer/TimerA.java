package com.gladurbad.antimovehack.check.impl.timer;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.event.FlyingEvent;
import com.gladurbad.antimovehack.event.ServerTeleportEvent;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TimerA extends Check {

    public TimerA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onFlying(FlyingEvent event) {
        final Player player = event.getPlayer();

        if(++data.timerTeleportTicks > 20) {
            final long time = System.currentTimeMillis();
            final long delay = time - data.timerLastTime;

            data.timerDifferences.add(delay);
            if (data.timerDifferences.size() >= 20) {
                double timerAverage = data.timerDifferences.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
                double timerSpeed = 50 / timerAverage;
                if (Math.abs(timerSpeed - 1.0D) >= 0.1) {
                    AlertUtils.handleViolation(data, "Timer (A)", data.timerViolationLevel++, null);
                }
                data.timerDifferences.clear();
            }
            data.timerLastTime = time;
        }
    }

    @EventHandler
    public void onTeleport(ServerTeleportEvent event) {
        final Player player = event.getPlayer();
        data.timerTeleportTicks = 0;
    }
}
