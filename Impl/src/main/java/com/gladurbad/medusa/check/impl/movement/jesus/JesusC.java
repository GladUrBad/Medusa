package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CheckInfo(name = "Jesus", type = "C")
public class JesusC extends Check {

    private int inWaterTicks;

    public JesusC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && isFlyingPacket(packet)) {
            final Player player = data.getPlayer();

            final boolean inWater= CollisionUtil.isOnChosenBlock(player, 0.1, Material.WATER, Material.STATIONARY_WATER) &&
                    !CollisionUtil.isOnSolid(player);

            if (inWater) {
                if (++inWaterTicks > 10) {
                    if ((data.getLastDeltaXZ() > 0.15 || Math.abs(data.getDeltaY()) > 0.15) && inWaterTicks > 10) {
                        increaseBuffer();
                        if (buffer > 5) fail();
                    } else {
                        decreaseBuffer();
                        setLastLegitLocation(data.getPlayer().getLocation());
                    }
                }
            } else {
                inWaterTicks = 0;
            }
        }
    }
}
