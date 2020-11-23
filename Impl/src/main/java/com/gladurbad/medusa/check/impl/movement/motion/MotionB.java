package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.enums.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "B")
public class MotionB extends Check {

    private int slimeTicks;
    private boolean slime;
    public MotionB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();
            float expectedJumpMotion = 0.42F;

            if (PacketEvents.getAPI().getServerUtils().getVersion() == ServerVersion.v_1_7_10) {
                slime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.ICE, Material.PACKED_ICE);
            } else if (PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_7_10) && PacketEvents.getAPI().getServerUtils().getVersion().isLowerThan(ServerVersion.v_1_9)) {
                slime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5001, Material.ICE, Material.PACKED_ICE, Material.SLIME_BLOCK);
            }

            if (slime) {
                slimeTicks = 0;
            }

            final boolean jumped = CollisionUtil.isOnGround(data.getLastBukkitLocation(), -0.00001) &&
                    !CollisionUtil.isOnGround(data.getBukkitLocation(), -0.00001) &&
                    data.getDeltaY() > 0;

            final boolean valid = jumped && !CollisionUtil.isCollidingWithClimbable(player) &&
                    data.getLastLocation().getY() % 1 == 0 &&
                    !CollisionUtil.blockNearHead(data.getBukkitLocation()) && ++slimeTicks > 20 && data.getTicksSinceVelocity() > 10;

            if (PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) > 0) {
                expectedJumpMotion += PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
            }

            if (data.getDeltaY() < expectedJumpMotion && valid) {
                fail();
            } else {
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
