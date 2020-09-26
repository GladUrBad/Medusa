package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Killaura", type = "F", dev = true)
public class KillauraF extends Check {

    private static final ConfigValue maxAngle = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-angle");
    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    public KillauraF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            final double deltaX = useEntity.getEntity().getLocation().getX() - data.getPlayer().getLocation().getX();
            final double deltaZ = useEntity.getEntity().getLocation().getZ() - data.getPlayer().getLocation().getZ();

            final double directionX = -Math.sin(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;
            final double directionZ = Math.cos(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F;

            final Vector direction = new Vector(directionX, 0, directionZ);
            final Vector positionDifference = new Vector(deltaX, 0, deltaZ);

            final double angle = positionDifference.angle(direction);

            if (Math.abs(positionDifference.getX()) > 1.5 || Math.abs(positionDifference.getZ()) > 1.5) {
                if (angle > maxAngle.getDouble()) {
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
