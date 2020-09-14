package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CheckInfo(name = "Jesus", type = "A")
public class JesusA extends Check {

    public JesusA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            final boolean invalid = CollisionUtil.isOnChosenBlock(player, -0.1, Material.WATER, Material.STATIONARY_WATER) &&
                    CollisionUtil.isOnChosenBlock(player, 0.1, Material.AIR) &&
                    !CollisionUtil.isOnSolid(player) &&
                    !CollisionUtil.isOnLilyOrCarpet(player);

            if (invalid) {
                increaseBuffer();
                if (buffer > 10) {
                    fail();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(player.getLocation());
            }
        }
    }
}
