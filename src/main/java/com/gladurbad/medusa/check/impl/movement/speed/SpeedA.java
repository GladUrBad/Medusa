package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import org.bukkit.entity.Player;

@CheckInfo(name = "Speed", type = "A", dev = false)
public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && isFlyingPacket(packet)) {
            final Player player = data.getPlayer();

            final double deltaXZ = data.getDeltaXZ();
            final double lastDeltaXZ = data.getLastDeltaXZ();

            final double EPSILON = 0.027;
            final double prediction = lastDeltaXZ * 0.91F;
            final double difference = Math.abs(prediction - deltaXZ);

            if (difference > EPSILON && !CollisionUtil.isOnGround(player)) {
                increaseBuffer();
                if (buffer > 5) {
                    fail();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(player.getLocation());
            }
        }
    }
}
