package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed (C)", description = "Basic verbose speed check.")
public class SpeedC extends Check {

    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double maxSpeed = getSpeed(0.4);

            final boolean exempt = isExempt(
                    ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.PISTON, ExemptType.VELOCITY,
                    ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDERBLOCK,
                    ExemptType.ICE
            );

            if (deltaXZ > maxSpeed && !exempt) {
                buffer += buffer < 15 ? 1 : 0;
                if (buffer > 10) {
                    fail("deltaXZ=" + deltaXZ + " max=" + maxSpeed);
                    buffer /= 2;
                }
            } else {
                buffer -= buffer > 0 ? 0.25 : 0;
            }
        }
    }

    private double getSpeed(double movement) {
        if (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) > 0) {
            movement *= 1.0D + 0.2D * (double)(PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
        }
        return movement;
    }
}
