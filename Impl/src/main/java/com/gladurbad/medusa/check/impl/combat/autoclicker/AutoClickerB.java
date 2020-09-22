package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.customtype.EvictingList;
import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

@CheckInfo(name = "AutoClicker", type = "B", dev = true)
public class AutoClickerB extends Check {

    private int ticks;
    private ArrayDeque<Double> samples = new ArrayDeque<>();

    public AutoClickerB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isSwing() && !data.isDigging()) {
            if (ticks < 10) {
                samples.add((double) ticks * 50);

                if (samples.size() >= 100) {
                    final double deviation = MathUtil.getStandardDeviation(samples);
                    samples.clear();
                }
            }
            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
