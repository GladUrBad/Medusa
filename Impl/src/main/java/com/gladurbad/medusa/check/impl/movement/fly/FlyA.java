package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Spiriten.
 */

@CheckInfo(name = "Fly (A)", description = "Checks for vertical movement manipulation.")
public final class FlyA extends Check {

    public FlyA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        //Glad, I wasnt planning on giving you my check for this since it follows the same basic guidelines as yours, but
        //looked at it you just exempt on velocity. N.o. Also you use a higher buffer, so this should flag quicker. Please
        //do note, this may still have falses. I have not tested it outside of my 0 ping localhost. Use with caution.
        //
        //It contains the normal vertical "prediction", partially-safer velocity handling, fall speed "prediction" (not
        // tested, just now it doesnt false), and a max fall speed check.

        if (packet.isPosition()) {

            //first one is obvious why we return, second because teleports can cause falses
            if (data.getPlayer().isFlying() || isExempt(ExemptType.TELEPORT)) return;

            //stolen from Glad's fly check.
            final boolean onGround = data.getPositionProcessor().getAirTicks() <= 6;

            //declaring these makes the code a bit clearer
            double deltaY = data.getPositionProcessor().getDeltaY();
            double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            //helps fix issues with jumping
            boolean jumped = lastDeltaY % (1D/64) == 0 && deltaY ==
                    0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F) &&
                    data.getPositionProcessor().isLastOnGround();

            //yeah, this is the generic vertical prediction
            double predicted = (lastDeltaY - 0.08) * 0.9800000190734863;
            //the player cannot physically move under -3.92 blocks per packet. if they do, they will be caught later in the check
            if (predicted < -3.92) predicted = -3.92;
            //minecraft sets the movement to 0 if it is under 0.005
            if (Math.abs(predicted) < 0.005) predicted = 0.0;
            //fixes a potential issue with jumping while landing
            if (jumped) predicted = 0.42F + (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F);

            //declare our fenceBelow boolean
            boolean fenceBelow = false;

            //check if the player has a fence/wall below them. can cause falses, wont deal with it.
            for (Block block : PlayerUtil.getNearbyBlocks(new Location(data.getPlayer().getWorld(), data.getPlayer().getLocation().getX(),
                    data.getPlayer().getLocation().getY() - 2, data.getPlayer().getLocation().getZ()), 1, 0, 1)) {
                if ((block.getType().toString().contains("FENCE")) || (block.getType().toString().contains("WALL"))) {
                    fenceBelow = true;
                }
            }

            //cleans up code. note: potential bypasses due to NEAR_VEHICLE and SLIME
            boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.INSIDE_VEHICLE, ExemptType.NEAR_VEHICLE,
                    ExemptType.CLIMBABLE, ExemptType.PLACING, ExemptType.PISTON, ExemptType.SLIME);

            //Check if the players deltaY is off from the predicted, theyre in the air, no fence below (can false due to
            //how i get if theyre in air), and are exempt from the things above
            if ((Math.abs(deltaY - predicted) > 0.001) && !onGround && !fenceBelow && !exempt &&
                    !data.getPositionProcessor().isOnWallOrFence()) {
                //if they arent taking velocity then we know theyre breaking our prediction
                if (!data.getVelocityProcessor().isTakingVelocity()) {
                    if (++buffer > 2) {
                        fail("1 exp=" + predicted + " got=" + deltaY + " tsv=" +
                                data.getVelocityProcessor().getTicksSinceVelocity() + " iat=" + data.getPositionProcessor().getAirTicks());
                    }
                    //i cant really accurately predict their deltaY with velocity in my current setup, so we make it a
                    //quite lenient by accepting their predicted + any vertical velocity.
                } else if (data.getVelocityProcessor().isTakingVelocity() &&
                        deltaY > (predicted + Math.abs(data.getVelocityProcessor().getVelocityY()))) {
                    //since they are violating, we still raise the buffer, this can potentially lead to excessively fast
                    //upward fly bypasses. solution? max Y speed check, but idfk how to do that
                    if (++buffer > 3) {
                        fail("2 exp=" + predicted + " got=" + deltaY + " tsv=" +
                                data.getVelocityProcessor().getTicksSinceVelocity() + " iat=" + data.getPositionProcessor().getAirTicks());
                    }
                    //handle buffer reduction
                } else buffer = Math.max(buffer - 0.15, 0);
                //handle buffer reduction
            } else buffer = Math.max(buffer - 0.15, 0);

            //
            // Potentially handle fall speed manipulation.
            //

            //make sure the player is off the ground and check if theyre previous going down/stable
            if (data.getPositionProcessor().getAirTicks() > 0 && lastDeltaY <= 0) {

                //establish our accuracy. compare our deltaY to what we predicted
                double accuracy = predicted - deltaY;

                //check if the deltaY is within a 0.001 margin of our prediction
                if (accuracy > 0.001) {
                    fail("3 | exp=" + predicted + " got=" + deltaY + " acc=" + accuracy + " at=" +
                            data.getPositionProcessor().getAirTicks());
                }
            }

            //-3.92 should be the lowest speed a player can naturally go, so going below it is obviously fall speed
            //manipulation
            if (deltaY < -3.92) {
                fail("4 | exp=" + predicted + " got=" + deltaY + " at=" +
                        data.getPositionProcessor().getAirTicks());
            }
        }
    }
}
