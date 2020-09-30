package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Killaura", type = "G", dev = true)
public class KillauraG extends Check {

    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    public KillauraG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            final boolean invalid = !data.getPlayer().hasLineOfSight(useEntity.getEntity()) &&
                    useEntity.getEntity().getNearbyEntities(2, 2, 2).size() < 2;

            if (invalid) {
                if (increaseBuffer() > maxBuffer.getDouble()) {
                    fail();
                }
            } else {
                decreaseBufferBy(bufferDecay.getDouble());
            }
        }
    }
}
