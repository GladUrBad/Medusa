package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Motion", type = "F", dev = true)
public class MotionF extends Check {

    public MotionF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving()) {
            if (isFlyingPacket(packet)) {
                if (data.isSprinting()) {
                    final Player player = data.getPlayer();
                    final Vector positionDifference = new Vector(
                            data.getLocation().getX() - data.getLastLocation().getX(),
                            0,
                            data.getLocation().getZ() - data.getLastLocation().getZ());

                    final Vector direction = new Vector(
                            -Math.sin(player.getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F,
                            0,
                            Math.cos(player.getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F);

                    final double angle = positionDifference.distanceSquared(direction);

                    if(angle > 0.32) {
                        increaseBuffer();
                        if(buffer > 10) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(2);
                        setLastLegitLocation(data.getLocation());
                    }
                }
                //debug(data.isSprinting());
            }
        }
    }
}
