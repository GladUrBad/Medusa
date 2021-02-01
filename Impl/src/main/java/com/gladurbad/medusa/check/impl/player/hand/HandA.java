package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;

/**
 * Created on 1/18/2021 Package com.gladurbad.medusa.check.impl.player.hand by GladUrBad
 */

@CheckInfo(name = "Hand (A)", experimental = true, description = "Checks for face occlusion when placing blocks.")
public final class HandA extends Check {

    public HandA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
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

            if (!validInteraction) {
                assert direction != null;
                fail("face=" + direction.getValue());
            }
        }
    }

    private Location getBlockAgainst(final Direction direction, final Location blockLocation) {
        if (Direction.UP.equals(direction)) {
            return blockLocation.clone().add(0, -1, 0);
        } else if (Direction.DOWN.equals(direction)) {
            return blockLocation.clone().add(0, 1, 0);
        } else if (Direction.EAST.equals(direction) || Direction.SOUTH.equals(direction)) {
            return blockLocation;
        } else if (Direction.WEST.equals(direction)) {
            return blockLocation.clone().add(1, 0, 0);
        } else if (Direction.NORTH.equals(direction)) {
            return blockLocation.clone().add(0, 0, 1);
        }
        return null;
    }
    private boolean interactedCorrectly(Location block, Location player, Direction face) {
        if (Direction.UP.equals(face)) {
            return player.getY() > block.getY();
        } else if (Direction.DOWN.equals(face)) {
            return player.getY() < block.getY();
        } else if (Direction.WEST.equals(face)) {
            return player.getX() < block.getX();
        } else if (Direction.EAST.equals(face)) {
            return player.getX() > block.getX();
        } else if (Direction.NORTH.equals(face)) {
            return player.getZ() < block.getZ();
        } else if (Direction.SOUTH.equals(face)) {
            return player.getZ() > block.getZ();
        }
        return true;
    }
}
