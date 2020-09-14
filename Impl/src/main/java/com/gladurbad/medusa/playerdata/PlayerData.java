package com.gladurbad.medusa.playerdata;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.manager.CheckManager;
import com.gladurbad.medusa.network.Packet;

import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
@Setter
public class PlayerData implements PacketListener {

    private final Player player;
    private final UUID playerUUID;
    private List<Check> checks;
    private Map<String, Integer> prevVL = new HashMap<>();
    private Map<String, List<Check>> types = new HashMap<>();

    public PlayerData(UUID playerUUID, Player player) {
        this.playerUUID = playerUUID;
        this.player = player;
        this.location = this.getPlayer().getLocation();
        this.lastLocation = this.getPlayer().getLocation();
        this.checks = CheckManager.loadChecks(this);
        for(Check check : checks){
            types.putIfAbsent(check.getCheckInfo().name(), new ArrayList<>());
            types.get(check.getCheckInfo().name()).add(check);
            prevVL.putIfAbsent(check.getCheckInfo().name(), 0);
        }
    }

    //Movement data.
    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    private float deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch;
    private Location lastLocation, location;
    private boolean isSprinting, isSneaking;
    private Vector lastVelocity = new Vector(0, 0, 0);


    //Teleportation & setback data.
    private long lastSetbackTime;

    //Velocity data.
    private int ticksSinceVelocity, maxVelocityTicks, velocityTicks;
    private short velocityID;
    private boolean verifyingVelocity;

    //Miscellanious data
    private boolean alerts;
    private boolean digging;
    private int ticks;

    public boolean isTakingKnockback() { return Math.abs(this.ticks - this.velocityTicks) < this.maxVelocityTicks; }

    public void processPacket(final Packet packet) {
        //Handle checks.
        checks.forEach(check -> check.handle(packet));
        //Process packet information.
        this.processInput(packet);

    }

    private void processInput(final Packet packet) {
        if (packet.isReceiving()) {
            if (packet.getPacketId() == PacketType.Client.POSITION || packet.getPacketId() == PacketType.Client.POSITION_LOOK) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final World world = this.getLocation().getWorld();
                final double x = wrappedPacketInFlying.getX();
                final double y = wrappedPacketInFlying.getY();
                final double z = wrappedPacketInFlying.getZ();
                final float yaw = wrappedPacketInFlying.getYaw();
                final float pitch = wrappedPacketInFlying.getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);
                Location lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            } else if (packet.getPacketId() == PacketType.Client.LOOK) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final World world = this.getLocation().getWorld();
                final double x = this.getLocation().getX();
                final double y = this.getLocation().getY();
                final double z = this.getLocation().getZ();
                final float yaw = wrappedPacketInFlying.getYaw();
                final float pitch = wrappedPacketInFlying.getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);
                Location lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            } else if (packet.getPacketId() == PacketType.Client.FLYING) {
                final World world = this.getLocation().getWorld();
                final double x = this.getLocation().getX();
                final double y = this.getLocation().getY();
                final double z = this.getLocation().getZ();
                final float yaw = this.getLocation().getYaw();
                final float pitch = this.getLocation().getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);
                Location lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            } else if (packet.getPacketId() == PacketType.Client.ENTITY_ACTION) {
                WrappedPacketInEntityAction wrappedPacketInEntityAction = new WrappedPacketInEntityAction(packet.getRawPacket());

                switch (wrappedPacketInEntityAction.getAction()) {
                    case START_SPRINTING:
                        this.isSprinting = true;
                        break;
                    case STOP_SPRINTING:
                        this.isSprinting = false;
                        break;
                    case START_SNEAKING:
                        this.isSneaking = true;
                        break;
                    case STOP_SNEAKING:
                        this.isSneaking = false;
                        break;
                }

            } else if (packet.getPacketId() == PacketType.Client.BLOCK_DIG) {
                final WrappedPacketInBlockDig wrappedPacketInBlockDig = new WrappedPacketInBlockDig(packet.getRawPacket());

                switch (wrappedPacketInBlockDig.getDigType()) {
                    case START_DESTROY_BLOCK:
                        this.digging = true;
                        break;
                    case STOP_DESTROY_BLOCK:
                    case ABORT_DESTROY_BLOCK:
                        this.digging = false;
                        break;
                }
            }else if (packet.getPacketId() == PacketType.Client.TRANSACTION) {
                final WrappedPacketInTransaction wrappedPacketInTransaction = new WrappedPacketInTransaction(packet.getRawPacket());

                if (this.verifyingVelocity && wrappedPacketInTransaction.getActionNumber() == this.velocityID) {
                    this.verifyingVelocity = false;

                    this.velocityTicks = this.ticks;
                    this.maxVelocityTicks = (int) (((lastVelocity.getX() + lastVelocity.getZ()) / 2 + 2) * 15);
                }
            }
        } else if (packet.isSending()) {
            if (packet.getPacketId() == PacketType.Server.ENTITY_VELOCITY) {
                final WrappedPacketOutEntityVelocity wrappedPacketOutEntityVelocity = new WrappedPacketOutEntityVelocity(packet.getRawPacket());

                if (wrappedPacketOutEntityVelocity.getEntity() == player) {
                    this.ticksSinceVelocity = 0;

                    final double velocityX = wrappedPacketOutEntityVelocity.getVelocityX();
                    final double velocityY = wrappedPacketOutEntityVelocity.getVelocityY();
                    final double velocityZ = wrappedPacketOutEntityVelocity.getVelocityZ();

                    this.lastVelocity = new Vector(velocityX, velocityY, velocityZ);

                    this.velocityID = (short)new Random().nextInt(32767);
                    this.verifyingVelocity = true;
                    PacketEvents.getAPI().getPlayerUtils().sendPacket(player, new WrappedPacketOutTransaction(0, velocityID, false));
                }
            }
        }
    }

    private void processLocation(Location location, Location lastLocation) {
        ++this.ticksSinceVelocity;
        ++this.ticks;

        this.lastLocation = lastLocation;
        this.location = location;

        double lastDeltaX = deltaX;
        double deltaX = location.getX() - lastLocation.getX();

        this.lastDeltaX = lastDeltaX;
        this.deltaX = deltaX;

        double lastDeltaY = deltaY;
        double deltaY = location.getY() - lastLocation.getY();

        this.lastDeltaY = lastDeltaY;
        this.deltaY = deltaY;

        double lastDeltaZ = deltaZ;
        double deltaZ = location.getZ() - lastLocation.getZ();

        this.lastDeltaZ = lastDeltaZ;
        this.deltaZ = deltaZ;

        double lastDeltaXZ = deltaXZ;
        double deltaXZ = location.clone().toVector().setY(0.0).distance(lastLocation.clone().toVector().setY(0.0));

        this.lastDeltaXZ = lastDeltaXZ;
        this.deltaXZ = deltaXZ;

        float lastDeltaYaw = deltaYaw;
        float deltaYaw = Math.abs(MathUtil.getAngleDiff(location.getYaw(), lastLocation.getYaw()));

        this.lastDeltaYaw = lastDeltaYaw;
        this.deltaYaw = deltaYaw;

        float lastDeltaPitch = deltaPitch;
        float deltaPitch = Math.abs(location.getPitch() - lastLocation.getPitch());

        this.lastDeltaPitch = lastDeltaPitch;
        this.deltaPitch = deltaPitch;
    }
}