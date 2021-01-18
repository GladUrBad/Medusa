package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Predicate;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

/**
 * Exempt list:
 *
 * Ladders/other climbable blocks (vines)
 * Water
 * Under blocks
 * Velocity (Velocity A will handle any discrepancies.)
 * Teleports (fix your shitty teleport manager)
 * Webs (handle web movement in Fly A?)
 */

@CheckInfo(name = "Fly (A)", description = "Checks for gravity.")
public class FlyA extends Check {

    private final Predicate<Double> onGround = posY -> posY % 1D/64 == 0;

    public FlyA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            double prediction = (lastDeltaY - 0.08) * 0.98F;
            if (Math.abs(prediction) < 0.005) prediction = 0;
            final double difference = Math.abs(deltaY - prediction);

            final boolean invalid = difference > 0.001D &&
                    //Retarded collision processor makes me do dumb shit that could make bypasses like this.
                    !(data.getPositionProcessor().getY() % 0.5 == 0 && data.getPositionProcessor().isOnGround() && lastDeltaY < 0) &&
                    data.getPositionProcessor().isInAir() &&
                    !data.getPositionProcessor().isNearBoat() &&
                    !isExempt(ExemptType.TELEPORT) &&
                    !data.getPlayer().isFlying() &&
                    !data.getPlayer().isInsideVehicle() &&
                    data.getPositionProcessor().getAirTicks() > 5 &&
                    !data.getVelocityProcessor().isTakingVelocity();

            if (invalid) {
                buffer += buffer < 5 ? 1 : 0;
                if (buffer > 3) {
                    fail(String.format("diff=%.4f, buffer=%.2f", difference, buffer));
                }
            } else {
                buffer = Math.max(buffer - 0.1, 0);
            }
        }
    }
}
