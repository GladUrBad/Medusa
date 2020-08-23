package com.gladurbad.antimovehack.check.impl.motion;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;
import com.gladurbad.antimovehack.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "B", dev = true)
public class MotionB extends Check {

    private int slimeTicks;
    public MotionB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final Player player = data.getPlayer();
            float expectedJumpMotion = 0.42F;

            final boolean slime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.ICE, Material.PACKED_ICE, Material.SLIME_BLOCK);
            if(slime) slimeTicks = 0;

            final boolean jumped = CollisionUtil.isOnGround(data.getLastLocation(), -0.00001) && !CollisionUtil.isOnGround(data.getLocation(), -0.00001) && data.getDeltaY() > 0;

            final boolean valid = jumped && !CollisionUtil.isCollidingWithClimbable(player) && data.getLastLocation().getY() % 1 == 0 && !CollisionUtil.blockNearHead(data.getLocation()) && ++slimeTicks > 20;

            if(PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) > 0) {
                expectedJumpMotion += PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
            }

            if(data.getDeltaY() < expectedJumpMotion && valid) {
                failAndSetback();
            } else {
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
