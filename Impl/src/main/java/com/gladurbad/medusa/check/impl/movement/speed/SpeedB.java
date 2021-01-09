package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.data.processor.VelocityProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import org.bukkit.potion.PotionEffectType;

/**
 * Created on 12/19/2020 Package com.gladurbad.medusa.check.impl.movement.speed by GladUrBad
 */

@CheckInfo(name = "Speed (B)", description = "Checks for movement speed.")
public class SpeedB extends Check {

    private int groundTicks, airTicks;

    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition() && !isExempt(ExemptType.TELEPORT, ExemptType.FLYING)) {
            final WrappedPacketInFlying flying = new WrappedPacketInFlying(packet.getRawPacket());

            //We make this because the flag method of this check works a little bit differently.
            //This helps me avoid if else chains for flags/nested ternary operators.
            double speed = 0.0D;

            groundTicks = flying.isOnGround() ? groundTicks + 1 : 0;
            airTicks = !flying.isOnGround() ? airTicks + 1 : 0;
            
            final PositionProcessor position = positionInfo();
            final VelocityProcessor velocity = velocityInfo();
            
            final double deltaXZ = Math.abs(position.getDeltaXZ());
            final double deltaY = position.getDeltaY();

            //Return here to prevent it from running the calculations pointlessly.
            if (deltaXZ == 0) return;

            double maxGroundSpeed = getSpeed(0.287D);
            double maxAirSpeed = getSpeed(0.362D);
            double maxAfterJumpAirSpeed = getAfterJumpSpeed();

            final int sinceIceTicks = position.getSinceIceTicks();
            final int sinceSlimeTicks = position.getSinceSlimeTicks();
            final int sinceUnderBlockTicks = position.getSinceBlockNearHeadTicks();

            final boolean velocityExempt = isExempt(ExemptType.VELOCITY);

            //Handle velocity speed increase (incorrectly but this whole check is improper)
            if (velocityExempt) {
                final double velocityXz = Math.hypot(velocity.getVelocityX(), velocity.getVelocityZ()) + 0.5;
                maxAirSpeed += velocityXz;
                maxGroundSpeed += velocityXz;
            }

            //Handle jumping speed increase.
            if (deltaY > 0.4199 && airTicks == 1) {
                speed = deltaXZ/maxAfterJumpAirSpeed;
            }

            //Handle max air speed checking. (airTicks > 1 because of jumping increase first tick)
            //Handle max air speed increase based on edge cases. (e.g. sprint jump on ice, under block, slime block)
            if (airTicks > 1 || (airTicks > 0 && deltaY < 0.4199)) {
                if (sinceUnderBlockTicks <= 15) maxAirSpeed += 0.3;
                if (sinceIceTicks <= 15 || sinceSlimeTicks <= 10) maxAirSpeed += 0.25;
                speed = deltaXZ/maxAirSpeed;
            }

            //Handle max ground speed checking. (groundTicks > 1 because of landing speed increase)
            //Landing speed increase lasts for a few ticks (5-7) so check for that.
            if (groundTicks > 0) {
                if (groundTicks < 7) maxGroundSpeed += 0.17;
                if (sinceUnderBlockTicks <= 15) maxGroundSpeed += 0.15;
                if (sinceIceTicks <= 15 || sinceSlimeTicks <= 10) maxGroundSpeed += 0.2;
                speed = deltaXZ/maxGroundSpeed;
            }

            //debug("buffer=" + buffer);

            final double shiftedSpeed = Math.round(speed * 100);

            if (shiftedSpeed > 100) {
                if ((buffer += shiftedSpeed > 150 ? 60 : 20) > 100 || shiftedSpeed > 1000) {
                    fail(String.format(
                            "speed=%o%%, buffer=%.2f",
                            Math.round(speed * 100), buffer
                    ));
                    //Prevents the buffer from going too high and becoming redundant.
                    buffer = Math.min(350, buffer);
                }
            } else {
                buffer = Math.max(buffer - 1, 0);
            }
        }
    }

    //Stolen from Artemis Client, could be quite inaccurate.
    private double getSpeed(double movement) {
        if (PlayerUtil.getPotionLevel(player(), PotionEffectType.SPEED) > 0) {
            movement *= 1.0D + 0.2D * (double)(PlayerUtil.getPotionLevel(player(), PotionEffectType.SPEED));
        }
        return movement;
    }

    //Slightly inaccurate, maybe going to improve the math on this one more later.
    private double getAfterJumpSpeed() {
        return 0.62 + 0.033 * (double) (PlayerUtil.getPotionLevel(player(), PotionEffectType.SPEED));
    }
}
