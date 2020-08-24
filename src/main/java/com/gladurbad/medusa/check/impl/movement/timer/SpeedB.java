package com.gladurbad.medusa.check.impl.movement.timer;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;

import org.bukkit.Material;

@CheckInfo(name = "Speed", type = "B", dev = true)
public class SpeedB extends Check {

    private int iceSlimeTicks, underBlockTicks;

    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            double limit = PlayerUtil.getBaseSpeed(data.getPlayer());

            final boolean iceSlime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.ICE, Material.PACKED_ICE, Material.SLIME_BLOCK);
            final boolean underBlock = CollisionUtil.blockNearHead(data.getLocation());

            if(iceSlime) iceSlimeTicks = 0;
            if(underBlock) underBlockTicks = 0;

            if(++iceSlimeTicks < 40) limit += 0.34;
            if(++underBlockTicks < 40) limit += 0.7;

            final boolean invalid = !data.getPlayer().isFlying() && data.getDeltaXZ() > limit;

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
}
