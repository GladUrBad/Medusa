package com.gladurbad.medusa.packet.processor;

import com.gladurbad.medusa.data.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import com.gladurbad.medusa.packet.Packet;

public class ReceivingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            data.getActionProcessor().handleEntityAction(wrapper);
        }
        if (packet.isBlockDig()) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());

            data.getActionProcessor().handleBlockDig(wrapper);
        }
        if (packet.isClientCommand()) {
             final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());

             data.getActionProcessor().handleClientCommand(wrapper);
        }
        if (packet.isBlockPlace()) {
            data.getActionProcessor().handleBlockPlace();
        }
        if (packet.isHeldItemSlot()) {
            final WrappedPacketInHeldItemSlot wrapper = new WrappedPacketInHeldItemSlot(packet.getRawPacket());

            data.getActionProcessor().handleHeldItemSlot(wrapper);
        }
        if (packet.isCloseWindow()) {
            data.getActionProcessor().handleCloseWindow();
        }
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            data.getCombatProcessor().handleUseEntity(wrapper);
        }
        if (packet.isFlying()) {
            data.getActionProcessor().handleFlying();
            data.getVelocityProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();
        }
        if (packet.isPosition()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.getPositionProcessor().handle(wrapper.getX(), wrapper.getY(), wrapper.getZ(), wrapper.isOnGround());
        }
        if (packet.isRotation()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());
        }
        if (packet.isArmAnimation()) {
            data.getClickProcessor().handleArmAnimation();
            data.getActionProcessor().handleArmAnimation();
            data.getCombatProcessor().handleArmAnimation();
        }
        if (packet.isIncomingTransaction()) {
            final WrappedPacketInTransaction wrapper = new WrappedPacketInTransaction(packet.getRawPacket());

            data.getVelocityProcessor().handleTransaction(wrapper);
        }
        if (!data.getPlayer().hasPermission("medusa.bypass")) {
            data.getChecks().forEach(check -> check.handle(packet));
        }
    }
}