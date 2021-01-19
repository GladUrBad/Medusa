package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;

/**
 * Created on 11/23/2020 Package com.gladurbad.medusa.check.impl.combat.velocity by GladUrBad
 */
@CheckInfo(name = "Velocity (A)", experimental = true, description = "Checks for vertical velocity.")
public class VelocityA extends Check {

    private static final ConfigValue minVelPct = new ConfigValue(
            ConfigValue.ValueType.INTEGER, "minimum-velocity-percentage"
    );
    private static final ConfigValue maxVelPct = new ConfigValue(
            ConfigValue.ValueType.INTEGER, "maximum-velocity-percentage"
    );

    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.getVelocityProcessor().getTicksSinceVelocity() < 5) {
                final double deltaY = data.getPositionProcessor().getDeltaY();

                final double expectedDeltaY = data.getVelocityProcessor().getVelocityY();

                if (expectedDeltaY < 0) return;

                final int percentage = (int) Math.round((deltaY * 100.0) / expectedDeltaY);

                final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                        ExemptType.UNDER_BLOCK, ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.WEB);

                //On Ground checking is a bandaid fix. Check if the player stepped up blocks instead.
                final boolean invalid = !exempt
                        && (percentage < minVelPct.getInt() || percentage > maxVelPct.getInt())
                        && expectedDeltaY > 0 && !data.getPositionProcessor().isOnGround();

                if (invalid) {
                    if (++buffer > 5) {
                        buffer = 0;
                        fail();
                    }
                } else {
                    buffer = 0;
                }
            }
        }
    }
}
