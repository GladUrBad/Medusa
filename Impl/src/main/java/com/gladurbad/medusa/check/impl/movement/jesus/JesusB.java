package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.Material;

@CheckInfo(name = "Jesus (B)", description = "Checks for water friction.", experimental = true)
public class JesusB extends Check {

    private int ticks;

    public JesusB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition() && !isExempt(ExemptType.VELOCITY) && PlayerUtil.shouldCheckJesus(data)) {
            if (++ticks > 10) {
                final double prediction = data.getPositionProcessor().getLastDeltaXZ() * 0.8F;
                final double difference = Math.abs(data.getPositionProcessor().getLastDeltaXZ() - prediction);

                if (difference > 0.03) {
                    if (++buffer > 5) {
                        fail("diff=" + difference);
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }
                }
        } else {
            ticks = 0;
        }
    }
}
