package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Spiriten
 */
@CheckInfo(name = "Velocity (B)", experimental = true, description = "Checks for vertical velocity.")
public final class VelocityB extends Check {

    //max value just as nothings been set yet
    private int resetTick = Integer.MAX_VALUE;
    private double lastPrediction;

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {

        //
        // PLEASE NOTE
        //
        // This check can be rather easily bypassed as far as I know.
        // If they send a spoofed onGround packet at the right time the whole thing will not check.
        // Theres currently no other highly accuracy way to check if theyre on ground in this base.
        //

        if (packet.isFlying()) {
            //just cleans up code
            double velocityY = data.getVelocityProcessor().getVelocityY();
            int tsv = data.getVelocityProcessor().getTicksSinceVelocity();

            //make sure theyre actually taking velocity (> 0), are exempt from our guidelines above, and have taken
            //velocity after touching the ground
            if (velocityY > 0 && tsv < resetTick) {
                //i wont bother checking velocity in any of these scenarios
                boolean isExempt = (isExempt(ExemptType.TELEPORT, ExemptType.UNDER_BLOCK, ExemptType.INSIDE_VEHICLE,
                        ExemptType.NEAR_VEHICLE, ExemptType.CLIMBABLE, ExemptType.WEB));

                //just cleans up code
                double deltaY = data.getPositionProcessor().getDeltaY();
                double lastDeltaY = data.getPositionProcessor().getLastDeltaY();
                WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

                //if they touch the ground, stop checking for velocity. we use the wrapper in hopes our groundspoof
                //checks actually catch it and override it
                if (wrapper.isOnGround() && data.getPositionProcessor().isMathematicallyOnGround() || isExempt
                ) {
                    //set it to + 1 so it doesnt get stuck in a loop. if it resets at 0, how should it become less than
                    //0 to start again? this shouldnt allow it to run when it shouldnt as 0 + 1 = 1. the next tick is
                    //1. 1 is not less than 1.
                    resetTick = tsv + 1;
                    return;
                }

                //declare boolean and set default value
                boolean blockNextToPlayer = false;

                //its a lazy fix, but who cares? if one of these is next to the player, we stop the check as it can false it
                for (Block block : PlayerUtil.getNearbyBlocks(new Location(data.getPlayer().getWorld(), data.getPlayer().getLocation().getX(),
                        data.getPlayer().getLocation().getY() - 1, data.getPlayer().getLocation().getZ()), 1, 0, 1)) {
                    if ((block.getType().toString().contains("SLAB")) || (block.getType().toString().contains("FENCE")) ||
                            (block.getType().toString().contains("STAIR")) || (block.getType().toString().contains("BED")) ||
                            (block.getType().toString().contains("CAKE")) || (block.getType().toString().contains("WALL")) ||
                            (block.getType().toString().contains("SKULL")) || (block.getType().toString().contains("LAYER"))) {
                        blockNextToPlayer = true;
                    }
                }

                //stop the check if we have one of the blocks above next to the player
                if (blockNextToPlayer) {
                    //look at the first return thingy for explanation
                    resetTick = tsv + 1;
                    return;
                }



                //establish our prediction boolean and set it. if its been 1 tick since velocity, the player should
                //have the same velocity as was sent to them. otherwise, we take our last prediction (the velocity)
                //and run it through a generic air friction check. we use prediction instead of deltaY so we know what
                //it should be constantly
                double prediction = tsv == 1 ? velocityY : (lastPrediction - 0.08D) * 0.9800000190734863D;
                //the player cannot physically move under -3.92 blocks per packet. if they do,
                //they will be caught by FlyA
                if (prediction < -3.92) prediction = -3.92;
                //minecraft sets the movement to 0 if it is under 0.005
                if (Math.abs(prediction) < 0.005) prediction = 0.0;
                //jumping as taking velocity can cause issues. to see if this happens, check if they jumped using code
                //from MotionC. if they are, set their prediction to the jump height
                //
                //NOTE: I am not sure if this will create bypasses. Have fun Glad!
                if (data.getPositionProcessor().isLastOnGround() && deltaY == 0.42f +
                        (double) ((float) PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F)) {
                    prediction = 0.42f;}

                //if the difference of our deltaY and prediction is greater than 0.00001, the player is cheating
                if (Math.abs(deltaY - prediction) > 0.00001) {
                    fail("1 | exp=" + prediction + " got=" + deltaY +  " vy=" +
                            velocityY + " og=" + data.getPositionProcessor().isOnGround() + " tsv=" + tsv + " ia=" +
                            data.getPositionProcessor().isInAir() + " log=" + data.getPositionProcessor().isLastOnGround() +
                            " ldy=" + lastDeltaY);
                }

                //here we check if the player exceeds the velocity they should take. why is this important, theyre taking
                //more than what they should? because our flight check doesnt completely cover velocity. without this theres
                //a chance for a velocity flight bypass
                if (Math.abs(prediction - deltaY) > 1E-10) {
                    fail("2 | dif=" + Math.abs(prediction - deltaY));
                }

                //save our prediction to use it instead of our lastDeltaY
                lastPrediction = prediction;
            }
        }
    }
}