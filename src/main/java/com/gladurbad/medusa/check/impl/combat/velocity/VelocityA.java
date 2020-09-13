package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;

import java.util.ArrayDeque;

@CheckInfo(name = "Velocity", type = "A", dev = true)
public class VelocityA extends Check {

    private int velocityTicks;
    private double lastVelocity = 0;
    private ArrayDeque<Double> samples = new ArrayDeque<>();

    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && isFlyingPacket(packet)) {

            if (++velocityTicks < 6) {
                samples.add(data.getDeltaY());
            } else {
                if (samples.size() > 5) {
                    final double max = samples.stream().mapToDouble(value -> value).max().getAsDouble();
                    final double min = lastVelocity;

                    if (max < min) {
                        increaseBuffer();
                        if (buffer > 2) {
                            fail();
                        }
                    } else {
                        decreaseBuffer();
                    }
                    samples.clear();
                }
            }

        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.ENTITY_VELOCITY) {
            final WrappedPacketOutEntityVelocity velocity = new WrappedPacketOutEntityVelocity(packet.getRawPacket());

            lastVelocity = velocity.getVelocityY();
            velocityTicks = 0;
        }
    }
}
