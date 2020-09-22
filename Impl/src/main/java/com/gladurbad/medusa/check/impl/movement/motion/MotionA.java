package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

@CheckInfo(name = "Motion", type = "A", dev = true)
public class MotionA extends Check {

    private boolean teleported;

    public MotionA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if (!teleported) {
                double expectedJumpMotion = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F);

                final boolean jumped = data.getLastLocation().isOnGround() &&
                        !data.getLocation().isOnGround() && data.getDeltaY() > 0;

                final boolean notVelocity = data.getTicksSinceVelocity() > data.getMaxVelocityTicks();
                final boolean slime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.5, Material.SLIME_BLOCK);

                final boolean invalid = jumped &&
                        Math.abs(data.getDeltaY() - expectedJumpMotion) > 0.03 &&
                        notVelocity &&
                        !data.isRiptiding() &&
                        !CollisionUtil.blockNearHead(data.getBukkitLocation()) &&
                        !slime;

                if (invalid) {
                    fail();
                }
            }
            teleported = false;
        } else if (packet.isReceiving() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleported = true;
        }
    }
}
