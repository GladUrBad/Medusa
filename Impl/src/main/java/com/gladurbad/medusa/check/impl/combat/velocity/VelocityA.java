package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;

import java.util.ArrayDeque;


@CheckInfo(name = "Velocity", type = "A", dev = true)
public class VelocityA extends Check {

    private ArrayDeque<Double> samples = new ArrayDeque<>();
    private int weirdTicks;

    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.getTicksSinceVelocity() < 5) {
                final double taken = data.getDeltaY();
                final double expected = data.getLastVelocity().getY() * 0.99F;
                double percentage = (taken * 100) / expected;

                final boolean invalid = CollisionUtil.isCollidingWithClimbable(data.getPlayer()) ||
                        CollisionUtil.isInLiquid(data.getPlayer()) ||
                        CollisionUtil.blockNearHead(data.getPlayer().getLocation());

                if (invalid) {
                    weirdTicks = 0;
                } else {
                    ++weirdTicks;
                }

                samples.add(percentage);

                if (samples.size() >= 5 && weirdTicks > 20) {
                    if (samples.stream().mapToDouble(value -> value).max().orElse(0) < 100) {
                        if (increaseBuffer() > maxBuffer.getDouble()) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(bufferDecay.getDouble());
                    }
                    samples.clear();
                }
            }
        }
    }
}
