package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "A")
public class MotionA extends Check {

    private int slimeTicks, inVehicleTicks;

    public MotionA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            float expectedJumpMotion = 0.6F;

            final boolean slime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.SLIME_BLOCK);

            if (slime) {
                slimeTicks = 0;
            }

            final boolean jumped = CollisionUtil.isOnGround(data.getLastLocation(), -0.00001) &&
                    !CollisionUtil.isOnGround(data.getLocation(), -0.00001) &&
                    data.getDeltaY() > 0;

            final boolean inVehicle = data.getPlayer().isInsideVehicle();

            if (inVehicle) {
                inVehicleTicks = 0;
            } else {
                inVehicleTicks++;
            }

            if (PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) > 0) {
                expectedJumpMotion += PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
            }

            final boolean valid = ++slimeTicks > 20 && jumped && inVehicleTicks > 20;

            if (data.getDeltaY() > expectedJumpMotion && valid) {
                fail();
            } else {
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
