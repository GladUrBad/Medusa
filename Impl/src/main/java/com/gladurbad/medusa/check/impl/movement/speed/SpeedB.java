package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created on 12/19/2020 Package com.gladurbad.medusa.check.impl.movement.speed by GladUrBad
 */

@CheckInfo(name = "Speed (B)", description = "Checks for movement speed.")
public class SpeedB extends Check {

    private int groundTicks, airTicks;
    private double lastFlagBuffer;

    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition() && !isExempt(ExemptType.TELEPORT, ExemptType.FLYING)) {
            final WrappedPacketInFlying flying = new WrappedPacketInFlying(packet.getRawPacket());
            groundTicks = flying.isOnGround() ? groundTicks + 1 : 0;
            airTicks = !flying.isOnGround() ? airTicks + 1 : 0;

            final Player player = data.getPlayer();
            final PositionProcessor pos = data.getPositionProcessor();
            
            final double deltaXZ = Math.abs(pos.getDeltaXZ());
            final double lastDeltaXz = Math.abs(pos.getLastDeltaXZ());
            final double deltaY = pos.getDeltaY();
            final double lastDeltaY = pos.getLastDeltaY();

            //Return here to prevent it from running the calculations pointlessly.
            if (deltaXZ == 0) return;

            final boolean onGround = pos.isMathematicallyOnGround();

            double maxGroundSpeed = getSpeed(0.287D);
            double maxAirSpeed = getSpeed(0.362D);
            double maxAfterJumpAirSpeed = getAfterJumpSpeed(0.62D);

            final int sinceIceTicks = pos.getSinceIceTicks();
            final int sinceSlimeTicks = pos.getSinceSlimeTicks();
            final int sinceUnderBlockTicks = pos.getSinceBlockNearHeadTicks();

            final boolean velocityExempt = isExempt(ExemptType.VELOCITY);

            //Handle velocity speed increase (incorrectly but this whole check is improper)
            if (velocityExempt) {
                final double velocityX = data.getVelocityProcessor().getVelocityX();
                final double velocityZ = data.getVelocityProcessor().getVelocityZ();
                final double velocityXz = Math.hypot(velocityX, velocityZ);
                maxAirSpeed += velocityXz + 0.5;
                maxGroundSpeed += velocityXz + 0.5;
            }

            //Handle jumping speed increase.
            if (deltaY > 0.4199 && airTicks == 1) {
                if (deltaXZ > maxAfterJumpAirSpeed) {
                    //debug(String.format("JUMP: dxz=%.4f, mas=%.4f", deltaXZ, maxAfterJumpAirSpeed));
                    increaseBuffer();
                } else {
                    decreaseBuffer(0.05);
                }
            }

            //Handle max air speed checking. (airTicks > 1 because of jumping increase first tick)
            //Handle max air speed increase based on edge cases. (e.g. sprint jump on ice, under block, slime block)
            if (airTicks > 1 || (airTicks > 0 && deltaY < 0.4199)) {
                if (sinceUnderBlockTicks <= 15) maxAirSpeed += 0.3;
                if (sinceIceTicks <= 15 || sinceSlimeTicks <= 10) maxAirSpeed += 0.25;

                if (deltaXZ > maxAirSpeed) {
                    //debug(String.format("AIR: dxz=%.4f, mas=%.4f", deltaXZ, maxAirSpeed));
                    increaseBuffer();
                } else {
                    decreaseBuffer(0.05);
                }
            }

            //Handle max ground speed checking. (groundTicks > 1 because of landing speed increase)
            //Landing speed increase lasts for a few ticks (5-7) so check for that.
            if (groundTicks > 0) {
                if (groundTicks < 7) maxGroundSpeed += 0.17;
                if (sinceUnderBlockTicks <= 15) maxGroundSpeed += 0.15;
                if (sinceIceTicks <= 15 || sinceSlimeTicks <= 10) maxGroundSpeed += 0.2;

                if (deltaXZ > maxGroundSpeed) {
                    //debug(String.format("GROUND: dxz=%.4f, mas=%.4f", deltaXZ, maxGroundSpeed));
                    increaseBuffer();
                } else {
                    decreaseBuffer(0.05);
                }
            }

            if (getBuffer() >= 15) setBuffer(15);

            //Because of the funny way I made this check my flag method seems a little weird.
            if (getBuffer() > 7.5 && getBuffer() >= lastFlagBuffer) {
                if (airTicks > 1) {
                    fail(String.format("AIR: buffer=%o, speed=%.2f", Math.round(getBuffer()), (deltaXZ/maxAirSpeed)));
                } else if (airTicks == 1) {
                    fail(String.format("JUMP: buffer=%o, speed=%.2f", Math.round(getBuffer()), (deltaXZ/maxAfterJumpAirSpeed)));
                } else if (groundTicks > 0) {
                    fail(String.format("GROUND: buffer=%o, speed=%.2f", Math.round(getBuffer()), (deltaXZ/maxGroundSpeed)));
                }
                lastFlagBuffer = getBuffer();
            }
        }
    }

    //Stolen from Artemis Client, could be quite inaccurate.
    private double getSpeed(final double movement) {
        double baseSpeed = movement;
        if (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) > 0) {
            baseSpeed *= 1.0D + 0.2D * (double)(PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
        }
        return baseSpeed;
    }

    //Slightly inaccurate, maybe going to improve the math on this one more later.
    private double getAfterJumpSpeed(double movement) {
        return movement += 0.033 * (double) (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
    }
}
