package com.gladurbad.medusa.playerdata;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.manager.CheckManager;
import com.gladurbad.medusa.network.Packet;

import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.customtype.CustomLocation;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
@Setter
public class PlayerData implements PacketListener {

    private final Player player;
    private final UUID playerUUID;
    private List<Check> checks;

    public PlayerData(UUID playerUUID, Player player) {
        this.playerUUID = playerUUID;
        this.player = player;
        this.location = CustomLocation.fromBukkit(player.getLocation());
        this.lastLocation = this.location;
        this.checks = CheckManager.loadChecks(this);

        //Transaction ping system initialization.
        transactionID = (short) new Random().nextInt(32767);
        PacketEvents.getAPI().getPlayerUtils().sendPacket(player, new WrappedPacketOutTransaction(0, transactionID, false));
        transactionReply = System.currentTimeMillis();
    }

    //Movement data.
    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    private float deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch;
    private CustomLocation lastLocation, location;
    private boolean isSprinting, isSneaking, isInInventory;
    private Vector lastVelocity = new Vector(0, 0, 0);


    //Teleportation & setback data.
    private long lastSetbackTime, timeInInventory;

    //Velocity data.
    private int ticksSinceVelocity, maxVelocityTicks, velocityTicks;
    private short velocityID;
    private boolean verifyingVelocity;

    //Teleport data.

    //Miscellanious data.
    private boolean alerts;
    private boolean digging;
    private int ticks;
    private boolean shouldCheck = true;

    //Transaction pinging system data.
    private long transactionReply, transactionPing;
    private short transactionID;

    public boolean isTakingKnockback() { return Math.abs(this.ticks - this.velocityTicks) < this.maxVelocityTicks; }

    public void processPacket(final Packet packet) {
        //Handle checks.
        if (shouldCheck) {
            checks.forEach(check -> check.handle(packet));
        }
        //Process packet information.
        this.processInput(packet);

    }

    private void processInput(final Packet packet) {
        if (packet.isReceiving()) {
            if (packet.getPacketId() == PacketType.Client.POSITION || packet.getPacketId() == PacketType.Client.POSITION_LOOK) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final double x = wrappedPacketInFlying.getX();
                final double y = wrappedPacketInFlying.getY();
                final double z = wrappedPacketInFlying.getZ();
                final float yaw = wrappedPacketInFlying.getYaw();
                final float pitch = wrappedPacketInFlying.getPitch();
                final boolean onGround = wrappedPacketInFlying.isOnGround();

                CustomLocation location = new CustomLocation(x, y, z, yaw, pitch, onGround);
                CustomLocation lastLocation = getLocation() != null ? getLocation() : location;

                processLocation(location, lastLocation);
            } else if (packet.getPacketId() == PacketType.Client.LOOK) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final double x = this.getLocation().getX();
                final double y = this.getLocation().getY();
                final double z = this.getLocation().getZ();
                final float yaw = wrappedPacketInFlying.getYaw();
                final float pitch = wrappedPacketInFlying.getPitch();
                final boolean onGround = wrappedPacketInFlying.isOnGround();

                CustomLocation location = new CustomLocation(x, y, z, yaw, pitch, onGround);
                CustomLocation lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            } else if (packet.getPacketId() == PacketType.Client.FLYING) {
                final WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final double x = this.getLocation().getX();
                final double y = this.getLocation().getY();
                final double z = this.getLocation().getZ();
                final float yaw = this.getLocation().getYaw();
                final float pitch = this.getLocation().getPitch();
                final boolean onGround = wrappedPacketInFlying.isOnGround();

                CustomLocation location = new CustomLocation(x, y, z, yaw, pitch, onGround);
                CustomLocation lastLocation = this.getLocation() != null ? this.getLocation() : location;

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

                if (wrappedPacketInTransaction.getActionNumber() == transactionID) {
                    transactionPing = System.currentTimeMillis() - transactionReply;

                    transactionID = (short) new Random().nextInt(32767);
                    PacketEvents.getAPI().getPlayerUtils().sendPacket(player, new WrappedPacketOutTransaction(0, transactionID, false));
                    transactionReply = System.currentTimeMillis();
                }
            } else if (packet.getPacketId() == PacketType.Client.CLIENT_COMMAND) {
                final WrappedPacketInClientCommand wrappedPacketInClientCommand = new WrappedPacketInClientCommand(packet.getRawPacket());

                if (wrappedPacketInClientCommand.getClientCommand() == WrappedPacketInClientCommand.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    this.isInInventory = true;
                    this.timeInInventory = System.currentTimeMillis();
                }
            } else if (packet.getPacketId() == PacketType.Client.CLOSE_WINDOW) {
                if (this.isInInventory) {
                    this.isInInventory = false;
                    this.timeInInventory = 0;
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

    private void processLocation(CustomLocation location, CustomLocation lastLocation) {
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

    public Location getLastBukkitLocation() {
        return getLastLocation().toBukkit(getPlayer().getWorld());
    }

    public Location getBukkitLocation() {
        return getLocation().toBukkit(getPlayer().getWorld());
    }
}