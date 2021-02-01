package com.gladurbad.medusa.exempt.type;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.util.PlayerUtil;
import com.gladurbad.medusa.util.ServerUtil;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum ExemptType {

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(data.getPlayer().getLocation().getBlockX() << 4,
            data.getPlayer().getLocation().getBlockZ() << 4)),

    TPS(data -> ServerUtil.getTPS() < 18.5D),

    TELEPORT(data -> data.getPositionProcessor().isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 2000L),

    VELOCITY(data -> data.getVelocityProcessor().isTakingVelocity()),

    JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 5000L),

    TRAPDOOR(data -> data.getPositionProcessor().isNearTrapdoor()),

    STEPPED(data -> data.getPositionProcessor().isOnGround() && data.getPositionProcessor().getDeltaY() > 0),

    CINEMATIC(data -> data.getRotationProcessor().isCinematic()),

    SLIME(data -> data.getPositionProcessor().getSinceSlimeTicks() < 30),

    ICE(data -> data.getPositionProcessor().getSinceIceTicks() < 40),

    SLAB(data -> data.getPositionProcessor().isNearSlab()),

    STAIRS(data -> data.getPositionProcessor().isNearStairs()),

    WEB(data -> data.getPositionProcessor().isInWeb()),

    CLIMBABLE(data -> data.getPositionProcessor().isOnClimbable()),

    DIGGING(data -> Medusa.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastDiggingTick() < 10),

    BLOCK_BREAK(data -> Medusa.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBreakTick() < 10),

    PLACING(data -> Medusa.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastPlaceTick() < 10),

    NEAR_VEHICLE(data -> data.getPositionProcessor().isNearVehicle()),

    INSIDE_VEHICLE(data -> data.getPositionProcessor().getSinceVehicleTicks() < 20),

    LIQUID(data -> data.getPositionProcessor().isInLiquid()),

    UNDER_BLOCK(data -> data.getPositionProcessor().isBlockNearHead()),

    PISTON(data -> data.getPositionProcessor().getSinceNearPistonTicks() < 50),

    VOID(data -> data.getPlayer().getLocation().getY() < 4),

    COMBAT(data -> data.getCombatProcessor().getHitTicks() < 5),

    FLYING(data -> data.getPositionProcessor().getSinceFlyingTicks() < 40),

    AUTO_CLICKER(data -> data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING, ExemptType.BLOCK_BREAK)),

    DEPTH_STRIDER(data -> PlayerUtil.getDepthStriderLevel(data.getPlayer()) > 0);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}
