package com.gladurbad.antimovehack.check.impl.speed;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;
import com.gladurbad.antimovehack.util.PlayerUtil;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Speed", type = "B", dev = true)
public class SpeedB extends Check {

    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PlayerMoveEvent event) {
        double limit = PlayerUtil.getBaseSpeed(data.getPlayer()) + handleLimit(event);

        final boolean invalid = !data.getPlayer().isFlying() && data.deltaXZ > limit;

        if(invalid) {
            increaseBuffer();
            if(buffer > 7) {
                failAndSetback();
            }
        } else {
            decreaseBuffer();
            setLastLegitLocation(data.getPlayer().getLocation());
        }
    }

    public double handleLimit(PlayerMoveEvent event) {
        if(CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.ICE, Material.PACKED_ICE, Material.SLIME_BLOCK)) return 0.34;
        if(CollisionUtil.blockNearHead(event.getTo())) return 0.7;
        return 0.0;
    }
}
