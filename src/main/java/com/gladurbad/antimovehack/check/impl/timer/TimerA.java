package com.gladurbad.antimovehack.check.impl.timer;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.playerdata.PlayerData;

import com.google.common.collect.Lists;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Deque;

@CheckInfo(name = "Timer", type = "A", dev = true)
public class TimerA extends Check {

    private long lastTime;
    private Deque<Long> samples = Lists.newLinkedList();

    public TimerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PlayerMoveEvent event) {
        final long time = System.currentTimeMillis();
        final long delay = time - lastTime;

        if(time - data.lastSetbackTime < 10000L) return;

        samples.add(delay);
        if (samples.size() >= 20) {
            double timerAverage = samples.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
            double timerSpeed = 50 / timerAverage;

            if (Math.abs(timerSpeed - 1.0D) >= 0.1) fail();

            samples.clear();
        }
        lastTime = time;
    }
}
