package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.enums.ServerVersion;
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
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            final boolean inWater= CollisionUtil.isOnChosenBlock(player, 0.1, Material.WATER, Material.STATIONARY_WATER) &&
                    !CollisionUtil.isOnSolid(player) && !data.getPlayer().isInsideVehicle();

            if (inWater  && !data.getPlayer().isFlying()) {
                if (++inWaterTicks > 10) {
                    double maxDeltaXZ = PlayerUtil.getServerVersion().isHigherThan(ServerVersion.v_1_13) ? 0.196 : 0.15;
                    double maxDeltaY = PlayerUtil.getServerVersion().isHigherThan(ServerVersion.v_1_13) ? 0.394 : 0.15;
                    if ((data.getLastDeltaXZ() > maxDeltaXZ || Math.abs(data.getDeltaY()) > maxDeltaY) && inWaterTicks > 10) {
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
