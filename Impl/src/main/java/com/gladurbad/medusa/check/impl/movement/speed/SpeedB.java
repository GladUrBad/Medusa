package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.CollisionUtil;
import com.gladurbad.medusa.util.PlayerUtil;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.enums.ServerVersion;
import org.bukkit.Material;

@CheckInfo(name = "Speed", type = "B")
public class SpeedB extends Check {

    private int iceSlimeTicks, underBlockTicks, flyingTicks;
    private boolean iceSlime;

    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && packet.isFlying()) {
            double limit = PlayerUtil.getBaseSpeed(data.getPlayer());

            if (PacketEvents.getAPI().getServerUtils().getVersion().isLowerThan(ServerVersion.v_1_7_10)) {
                iceSlime = CollisionUtil.isOnChosenBlock(data.getPlayer(),
                        -0.5001, Material.ICE, Material.PACKED_ICE, Material.SLIME_BLOCK);
            } else {
                iceSlime = CollisionUtil.isOnChosenBlock(data.getPlayer(),
                        -0.5001, Material.ICE, Material.PACKED_ICE);
            }

            final boolean underBlock = CollisionUtil.blockNearHead(data.getBukkitLocation());
            final boolean flying = data.getPlayer().isFlying();

            if (flying) {
                flyingTicks = 0;
            } else {
                flyingTicks++;
            }

            if (iceSlime) {
                iceSlimeTicks = 0;
            }
            if (underBlock) {
                underBlockTicks = 0;
            }

            if (++iceSlimeTicks < 40) {
                limit += 0.34;
            }
            if (++underBlockTicks < 40) {
                limit += 0.7;
            }

            if (data.getTicksSinceVelocity() < 15) {
                limit += Math.hypot(Math.abs(data.getLastVelocity().getX()),
                        Math.abs(data.getLastVelocity().getZ()));
            }

            final boolean invalid = !data.getPlayer().isFlying()
                    && data.getDeltaXZ() > limit && flyingTicks > 40;

            if (invalid) {
                increaseBuffer();
                if (buffer >= 8) {
                    fail();
                }
            } else {
                buffer = 0;
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}
