package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;

import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.Location;

import java.text.DecimalFormat;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 */

@CheckInfo(name = "KillAura (D)", experimental = true, description = "Checks for high accuracy.")
public class KillAuraD extends Check {

    private int hits, swings;
    private Location lastLocation;
    private Float lastYaw, lastPitch;

    public KillAuraD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            final float yaw = data.getRotationProcessor().getYaw() % 360F;
            final float pitch = data.getRotationProcessor().getPitch();
            final boolean isMoving = data.getPositionProcessor().getDeltaXZ() != 0;
            if (wrapper.getEntity() != null) {
                if (lastLocation != null && lastYaw != null && lastPitch != null) {
                    if (wrapper.getEntity().getLocation().distance(lastLocation) > 0.1 && lastYaw != yaw && lastPitch != pitch && isMoving) {
                        ++hits;
                    }
                }
                lastLocation = wrapper.getEntity().getLocation();
            }
            lastYaw = yaw;
            lastPitch = pitch;
        } else if (packet.isArmAnimation()) {
            if (++swings >= 50) {
                final double accuracy = hits/swings;
                if (hits > 40) {
                    if (increaseBuffer(10) > 20) {
                        fail("accuracy - " + hits + " landed from a sample size of " + swings);
                    }
                } else {
                    decreaseBuffer(2);
                }
                hits = swings = 0;
            }
        }
    }
}
