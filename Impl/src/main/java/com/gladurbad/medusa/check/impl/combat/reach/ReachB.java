package com.gladurbad.medusa.check.impl.combat.reach;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach",  type = "B", dev = true)
public class ReachB extends Check {

    private static final ConfigValue maxReach = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-reach");
    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    public ReachB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (useEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final Vector playerLoc = data.getPlayer().getLocation().toVector().setY(0);
                final Vector victimLoc = useEntity.getEntity().getLocation().toVector().setY(0);

                final double distance = playerLoc.distance(victimLoc) - 0.57;

                if (distance > maxReach.getDouble()) {
                    if (increaseBuffer() > maxBuffer.getDouble()) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(bufferDecay.getDouble());
                }
            }
        }
    }
}
