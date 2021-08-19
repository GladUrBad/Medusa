package com.gladurbad.medusa.packet.processor;

import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.in.teleportaccept.WrappedPacketInTeleportAccept;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Bukkit;

public final class ReceivingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            data.getActionProcessor().handleEntityAction(wrapper);
        } else if (packet.isBlockDig()) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());

            data.getActionProcessor().handleBlockDig(wrapper);
        } else if (packet.isClientCommand()) {
             final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());

             data.getActionProcessor().handleClientCommand(wrapper);
        } else if (packet.isBlockPlace()) {
            data.getActionProcessor().handleBlockPlace();
        } else if (packet.isHeldItemSlot()) {
            final WrappedPacketInHeldItemSlot wrapper = new WrappedPacketInHeldItemSlot(packet.getRawPacket());
            data.getActionProcessor().handleHeldItemSlot(wrapper);
        } else if (packet.isCloseWindow()) {
            data.getActionProcessor().handleCloseWindow();
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            data.getCombatProcessor().handleUseEntity(wrapper);
        } else if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.getActionProcessor().handleFlying();
            data.getVelocityProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();

            if (wrapper.isMoving()) {
                data.getPositionProcessor().handle(wrapper.getPosition(), wrapper.isOnGround());
            }
            if (wrapper.isRotating()) {
                data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());
            }
        } else if (packet.isArmAnimation()) {
            data.getClickProcessor().handleArmAnimation();
            data.getActionProcessor().handleArmAnimation();
            data.getCombatProcessor().handleArmAnimation();
        } else if (packet.isIncomingTransaction()) {
            final WrappedPacketInTransaction wrapper = new WrappedPacketInTransaction(packet.getRawPacket());
            data.getVelocityProcessor().handleTransaction(wrapper);
        }
        if (!data.getPlayer().hasPermission("medusa.bypass") || data.getPlayer().isOp()) {
            data.getChecks().forEach(check -> check.handle(packet));
        }
    }
}