package com.gladurbad.medusa.data.processor;

import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.util.PlayerUtil;
import com.gladurbad.medusa.util.type.BoundingBox;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import io.github.retrooper.packetevents.utils.vector.Vector3d;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;

@Getter
public final class PositionProcessor {

    private final PlayerData data;

    private double x, y, z,
            lastX, lastY, lastZ,
            deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaZ, lastDeltaY, lastDeltaXZ;

    private boolean flying, inVehicle, inLiquid, inAir, inWeb, onWallOrFence,
            blockNearHead, onClimbable, onSolidGround, nearVehicle, onSlime, lastInAir,
            onIce, nearPiston, nearTrapdoor, nearSlab, nearStairs, teleporting;

    private int airTicks, sinceVehicleTicks, sinceFlyingTicks,
            groundTicks, sinceSlimeTicks, solidGroundTicks,
            iceTicks, sinceIceTicks, blockNearHeadTicks, sinceBlockNearHeadTicks,
            sinceNearPistonTicks, sinceTeleportTicks;

    private final ArrayDeque<Vector> teleports = new ArrayDeque<>();

    private Vector newTeleportingVec;

    private BoundingBox boundingBox;

    private boolean onGround, lastOnGround, mathematicallyOnGround;

    private final List<Block> blocks = new ArrayList<>();

    public PositionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final Vector3d position, final boolean onGround) {
        lastX = this.x;
        lastY = this.y;
        lastZ = this.z;
        this.lastOnGround = this.onGround;

        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
        this.onGround = onGround;

        handleCollisions();

        lastDeltaX = deltaX;
        lastDeltaY = deltaY;
        lastDeltaZ = deltaZ;
        lastDeltaXZ = deltaXZ;

        deltaX = this.x - lastX;
        deltaY = this.y - lastY;
        deltaZ = this.z - lastZ;
        deltaXZ = Math.hypot(deltaX, deltaZ);

        //in order to not run uselessly we have to check if we havent set the teleporting vector to null
        //this allows us to also not have any of that random code that was used in the previous teleport manager
        if (newTeleportingVec != null) {
            //the players packet response to the teleport should be sending the same coordinates back what it accepted.
            //if its not, then they are not actually teleporting
            if (newTeleportingVec.getX() == x && newTeleportingVec.getY() == y && newTeleportingVec.getZ() == z) {
                //set teleporting to true and set it to null so it wont run
                teleporting = true;
                newTeleportingVec = null;
                //if they did not resend the coordinates they were sent, its safe to say that they did not teleport
            } else teleporting = false;
            //if the teleporting vector is set to null, then its not been set by teleporting
        } else teleporting = false;

        mathematicallyOnGround = y % 0.015625 == 0.0;
    }

    public void handleTicks() {
        sinceTeleportTicks = teleporting ? 0 : sinceTeleportTicks + 1;
        groundTicks = onGround && mathematicallyOnGround ? groundTicks + 1 : 0;
        blockNearHeadTicks = blockNearHead ? blockNearHeadTicks + 1 : 0;
        sinceNearPistonTicks = nearPiston ? 0 : sinceNearPistonTicks + 1;
        sinceBlockNearHeadTicks = blockNearHead ? 0 : sinceBlockNearHeadTicks + 1;
        airTicks = inAir ? airTicks + 1 : 0;
        inVehicle = data.getPlayer().isInsideVehicle();
        sinceVehicleTicks = inVehicle ? 0 : sinceVehicleTicks + 1;
        iceTicks = onIce ? iceTicks + 1 : 0;
        sinceIceTicks = onIce ? 0 : sinceIceTicks + 1;
        solidGroundTicks = onSolidGround ? solidGroundTicks + 1 : 0;
        flying = data.getPlayer().isFlying();
        sinceFlyingTicks = flying ? 0 : sinceFlyingTicks + 1;
        sinceSlimeTicks = onSlime ? 0 : sinceSlimeTicks + 1;
    }

    public void handleCollisions() {
        blocks.clear();
        final BoundingBox boundingBox = new BoundingBox(data.getPlayer())
                .expandSpecific(0, 0, 0.55, 0.6, 0, 0);

        this.boundingBox = boundingBox;

        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();

        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 5) { //Expand max by 0.01 to compensate shortly for precision issues due to FP.
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    blocks.add(block);
                }
            }
        }

        handleClimbableCollision();
        handleVehicle();

        lastInAir = inAir;

        inLiquid = blocks.stream().anyMatch(Block::isLiquid);
        inWeb = blocks.stream().anyMatch(block -> block.getType() == Material.WEB);
        inAir = blocks.stream().allMatch(block -> block.getType() == Material.AIR);
        onIce = blocks.stream().anyMatch(block -> block.getType().toString().contains("ICE"));
        onSolidGround = blocks.stream().anyMatch(block -> block.getType().isSolid());
        nearSlab = blocks.stream().anyMatch(block -> block.getType().getData() == Step.class);
        nearStairs = blocks.stream().anyMatch(block -> block.getType().getData() == Stairs.class);
        nearTrapdoor = this.isCollidingAtLocation(1.801, material -> material == Material.TRAP_DOOR, CollisionType.ANY);
        //block near head may look like an overcomplicated shitfest now, but it works :shrug:
        //it should prevent all of those jumping under a trapdoor falses
        blockNearHead = blocks.stream().filter(block -> block.getLocation().getY() - data.getPositionProcessor().getY() > 1.7)
                .anyMatch(block -> block.getType() != Material.AIR)
                || this.isCollidingAtLocation(1.801, material -> material == Material.TRAP_DOOR, CollisionType.ANY)
                || this.isCollidingAtLocation(1.801, material -> material == Material.IRON_TRAPDOOR, CollisionType.ANY)
                || this.isCollidingAtLocation(2, material -> material != Material.AIR, CollisionType.ANY);
        onSlime = blocks.stream().anyMatch(block -> block.getType().toString().equalsIgnoreCase("SLIME_BLOCK"));
        nearPiston = blocks.stream().anyMatch(block -> block.getType().toString().contains("PISTON"));
        onWallOrFence = blocks.stream().anyMatch(block -> block.getType().toString().contains("WALL"))
                || blocks.stream().anyMatch(block -> block.getType().toString().contains("FENCE"));
        handleTicks();
    }

    public void handleClimbableCollision() {
        final Location location = data.getPlayer().getLocation();
        final int var1 = NumberConversions.floor(location.getX());
        final int var2 = NumberConversions.floor(location.getY());
        final int var3 = NumberConversions.floor(location.getZ());
        final Block var4 = this.getBlock(new Location(location.getWorld(), var1, var2, var3));
        this.onClimbable = var4.getType() == Material.LADDER || var4.getType() == Material.VINE;
    }


    public void handleVehicle() {
        nearVehicle = PlayerUtil.isNearVehicle(data.getPlayer());
    }

    public void handleServerPosition(final WrappedPacketOutPosition wrapper) {
        //set our teleport vec to save our teleport location
        newTeleportingVec = new Vector(wrapper.getPosition().getX(), wrapper.getPosition().getY(),
                wrapper.getPosition().getZ());
    }

    public boolean isColliding(CollisionType collisionType, Material blockType) {
        if (collisionType == CollisionType.ALL) {
            return blocks.stream().allMatch(block -> block.getType() == blockType);
        }
        return blocks.stream().anyMatch(block -> block.getType() == blockType);
    }

    public boolean isCollidingAtLocation(double drop, Predicate<Material> predicate, CollisionType collisionType) {
        final ArrayList<Material> materials = new ArrayList<>();

        for (double x = -0.3; x <= 0.3; x += 0.3) {
            for (double z = -0.3; z <= 0.3; z+= 0.3) {
                final Material material = getBlock(data.getPlayer().getLocation().clone().add(x, drop, z)).getType();
                if (material != null) {
                    materials.add(material);
                }
            }
        }

        return collisionType == CollisionType.ALL ? materials.stream().allMatch(predicate) : materials.stream().allMatch(predicate);
    }

    //Taken from Fiona. If you have anything better, please let me know, thanks.
    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(Medusa.INSTANCE.getPlugin(), futureTask);
            try {
                return futureTask.get();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }

    public enum CollisionType {
        ANY, ALL
    }
}
