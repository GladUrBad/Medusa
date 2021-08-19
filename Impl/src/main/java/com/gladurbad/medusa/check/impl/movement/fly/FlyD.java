package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by Spiriten.
 */

@CheckInfo(name = "Fly (D)", description = "Checks for spoofing onground value.")
public final class FlyD extends Check {

    public FlyD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            //dont care if theyre near a boat
            if (isExempt(ExemptType.NEAR_VEHICLE, ExemptType.PISTON, ExemptType.SLIME)) return;

            Player player = data.getPlayer();

            boolean onGround = data.getPositionProcessor().isOnGround();

            //check if they claim theyre onground twice while having an air block below them both times
            if (data.getPositionProcessor().isInAir() && data.getPositionProcessor().isLastInAir() &&
                    onGround && data.getPositionProcessor().isLastOnGround() &&
                    !data.getPositionProcessor().isNearStairs()) {
                //this check takes a while to flag now. it kept falsing on niche things ingame sadly
                if (++buffer > 3) {
                    fail("1");
                    //set the onground value to false to enforce fall damage
                    WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());
                    wrapper.setOnGround(false);
                }
            } else {
                buffer = Math.max(buffer - 0.5, 0);
            }

            //establish the potentiallyColliding boolean
            boolean potentiallyColliding = false;

            //check if the player has any blocks near them. this is to make a first tick groundspoof check
            for (Block block : PlayerUtil.getNearbyBlocks(new Location(player.getWorld(), player.getLocation().getX(),
                    player.getLocation().getY() - 1, player.getLocation().getZ()), 1, 1, 1)) {
                if (block.getType() != Material.AIR) {
                    potentiallyColliding = true;
                    break;
                }
            }

            //check if the player claims theyre onground and meet our colliding criteria
            if (data.getPositionProcessor().isOnGround() && !potentiallyColliding && !isExempt(ExemptType.PLACING,
                    ExemptType.LIQUID) && !data.getPositionProcessor().isOnSlime()) {
                fail("2");
                //set the onground value to false to enforce fall damage
                WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());
                wrapper.setOnGround(false);
            }
        }
    }
}
