package com.gladurbad.medusa.check.impl.movement.motion;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Spiriten.
 *
 * Completely redid the air movement check.
 *
 */
@CheckInfo(name = "Motion (E)", description = "Checks for switching direction mid-air.")
public final class MotionE extends Check {

    public MotionE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {

            //can false on velocity and piston, so just return
            if (isExempt(ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.PLACING, ExemptType.STEPPED,
                    ExemptType.NEAR_VEHICLE, ExemptType.FLYING)) return;

            //declare our booleans and set their default value
            boolean midAirSwitchX = false, midAirSwitchZ = false;

            //check if the player has an air block below them and arent clipping a block/placing blocks (can false if they
            //place below them usually)
            if (data.getPositionProcessor().isInAir()) {
                //check if their deltaX is less than or higher than their previous
                midAirSwitchX = data.getPositionProcessor().getDeltaX() != data.getPositionProcessor().getLastDeltaX();
                //check if their deltaZ is less than or higher than their previous
                midAirSwitchZ = data.getPositionProcessor().getDeltaZ() != data.getPositionProcessor().getLastDeltaZ();
            }

            //declare our fenceBelow boolean
            boolean fenceBelow = false;

            //the util is slightly intensive, so only run when weve triggered out midAirSwitchX/Z
            if (midAirSwitchX || midAirSwitchZ) {
                //check if the player has a fence/wall below them. can cause falses, wont deal with it.
                for (Block block : PlayerUtil.getNearbyBlocks(new Location(data.getPlayer().getWorld(), data.getPlayer().getLocation().getX(),
                        data.getPlayer().getLocation().getY() - 2, data.getPlayer().getLocation().getZ()), 1, 0, 1)) {
                    if ((block.getType().toString().contains("FENCE")) || (block.getType().toString().contains("WALL"))) {
                        fenceBelow = true;
                    }
                }
            }

            double differenceDeltaX = (Math.abs(data.getPositionProcessor().getDeltaX()) -
                    Math.abs(data.getPositionProcessor().getLastDeltaX()));

            //check if they meet our criteria of: midAirSwitchX, higher deltaX than previous deltaX (absolute), and
            //check if the deltaX is higher than lastDeltaX a certain amount. this is all necessary to prevent falses
            if (midAirSwitchX && (Math.abs(data.getPositionProcessor().getDeltaX()) >
                    Math.abs(data.getPositionProcessor().getLastDeltaX())) && !fenceBelow &&
                    differenceDeltaX > 0.024 && Math.abs(data.getPositionProcessor().getLastDeltaX()) > 0.003) {
                if (++buffer > 3) {
                    fail("2 " + differenceDeltaX + " " + data.getPositionProcessor().getLastDeltaZ());
                }
            } else {
                buffer = Math.max(buffer - 0.1, 0);
            }

            double differenceDeltaZ = (Math.abs(data.getPositionProcessor().getDeltaZ()) -
                    Math.abs(data.getPositionProcessor().getLastDeltaZ()));

            //check if they meet our criteria of: midAirSwitchZ, higher deltaZ than previous deltaZ (absolute), and
            //check if the deltaZ is higher than lastDeltaZ a certain amount. this is all necessary to prevent falses
            if (midAirSwitchZ && (Math.abs(data.getPositionProcessor().getDeltaZ()) >
                    Math.abs(data.getPositionProcessor().getLastDeltaZ())) && !fenceBelow &&
                    differenceDeltaZ > 0.024 && Math.abs(data.getPositionProcessor().getLastDeltaZ()) > 0.003) {
                if (++buffer > 3) {
                    fail("3 " + differenceDeltaZ + " " + data.getPositionProcessor().getLastDeltaZ());
                }
            } else {
                buffer = Math.max(buffer - 0.1, 0);
            }
        }
    }
}