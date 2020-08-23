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

    private int iceSlimeTicks, underBlockTicks;

    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PlayerMoveEvent event) {
        double limit = PlayerUtil.getBaseSpeed(data.getPlayer());

        final boolean iceSlime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.ICE, Material.PACKED_ICE, Material.SLIME_BLOCK);
        final boolean underBlock = CollisionUtil.blockNearHead(event.getTo());

        if(iceSlime) iceSlimeTicks = 0;
        if(underBlock) underBlockTicks = 0;

        if(++iceSlimeTicks < 40) limit += 0.34;
        if(++underBlockTicks < 40) limit += 0.7;

        final boolean invalid = !data.getPlayer().isFlying() && data.deltaXZ > limit;

        if(invalid) {
            increaseBuffer();
            if(buffer >= 7) {
                failAndSetback();
            }
        } else {
            buffer = 0;
            setLastLegitLocation(data.getPlayer().getLocation());
        }
    }
}
