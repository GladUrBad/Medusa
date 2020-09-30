package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.enums.ServerVersion;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "A", dev = true)
public class MotionA extends Check {

    private int teleportedTicks;
    private boolean slime;

    public MotionA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if (++teleportedTicks > 10) {
                double expectedJumpMotion = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F);

                final boolean jumped = data.getLastLocation().isOnGround() &&
                        !data.getLocation().isOnGround() && data.getDeltaY() > 0;

                final boolean notVelocity = data.getTicksSinceVelocity() > data.getMaxVelocityTicks();

                if (PacketEvents.getAPI().getServerUtils().getVersion().isLowerThan(ServerVersion.v_1_7_10)) {
                    slime = CollisionUtil.isOnChosenBlock(data.getPlayer(), -0.7, Material.SLIME_BLOCK);
                }

                final boolean invalid = jumped &&
                        Math.abs(data.getDeltaY() - expectedJumpMotion) > 0.03 &&
                        !CollisionUtil.blockNearHead(data.getPlayer().getLocation()) &&
                        notVelocity &&
                        !slime;

                if (invalid) {
                    fail();
                }
            }
        } else if (packet.isSending() && packet.getPacketId() == PacketType.Server.POSITION) {
            teleportedTicks = 0;
        }
    }
}
