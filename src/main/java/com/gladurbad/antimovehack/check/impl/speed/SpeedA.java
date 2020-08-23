package com.gladurbad.antimovehack.check.impl.speed;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;
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
                    failAndSetback();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(player.getLocation());
            }
        }
    }
}
