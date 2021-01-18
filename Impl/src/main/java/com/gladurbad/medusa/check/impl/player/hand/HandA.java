package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.enums.Direction;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import org.bukkit.Location;

/**
 * Created on 1/18/2021 Package com.gladurbad.medusa.check.impl.player.hand by GladUrBad
 */

@CheckInfo(name = "Hand (A)", experimental = true, description = "Checks for face occlusion when placing blocks.")
public class HandA extends Check {

    public HandA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final Direction direction = wrapper.getDirection();

            final Location blockLocation = new Location(
                    data.getPlayer().getWorld(),
                    wrapper.getX(),
                    wrapper.getY(),
                    wrapper.getZ()
            );

            final Location eyeLocation = data.getPlayer().getEyeLocation();
            final Location blockAgainstLocation = getBlockAgainst(direction, blockLocation);

            final boolean validInteraction = interactedCorrectly(blockAgainstLocation, eyeLocation, direction);

            if (!validInteraction) fail("face=" + direction);
        }
    }

    private Location getBlockAgainst(final Direction direction, final Location blockLocation) {
        switch (direction) {
            case UP:
                return blockLocation.clone().add(0, -1, 0);
            case DOWN:
                return blockLocation.clone().add(0, 1, 0);
            case EAST:
            case SOUTH:
                return blockLocation;
            case WEST:
                return blockLocation.clone().add(1, 0, 0);
            case NORTH:
                return blockLocation.clone().add(0, 0, 1);
            default:
                return null;
        }
    }
    private boolean interactedCorrectly(Location block, Location player, Direction face) {
        switch (face) {
            case UP:
                return player.getY() > block.getY();
            case DOWN:
                return player.getY() < block.getY();
            case WEST:
                return player.getX() < block.getX();
            case EAST:
                return player.getX() > block.getX();
            case NORTH:
                return player.getZ() < block.getZ();
            case SOUTH:
                return player.getZ() > block.getZ();
            default:
                return true;
        }
    }
}
