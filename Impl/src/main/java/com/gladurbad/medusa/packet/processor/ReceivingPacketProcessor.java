package com.gladurbad.medusa.packet.processor;

import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

public class ReceivingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {

        final Object rawPacket = packet.getRawPacket();

        if (packet.isFlying()) {
            data.getActionProcessor().handleFlying();
            data.getVelocityProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();
        }

        switch (packet.getPacketId()) {
            case PacketType.Client.ENTITY_ACTION:
                final WrappedPacketInEntityAction entityAction = new WrappedPacketInEntityAction(rawPacket);

                data.getActionProcessor().handleEntityAction(entityAction);
                break;
            case PacketType.Client.BLOCK_DIG:
                final WrappedPacketInBlockDig blockDig = new WrappedPacketInBlockDig(rawPacket);

                data.getActionProcessor().handleBlockDig(blockDig);
                break;
            case PacketType.Client.CLIENT_COMMAND:
                final WrappedPacketInClientCommand clientCommand = new WrappedPacketInClientCommand(rawPacket);

                data.getActionProcessor().handleClientCommand(clientCommand);
                break;
            case PacketType.Client.BLOCK_PLACE:
                data.getActionProcessor().handleBlockPlace();
                break;
            case PacketType.Client.CLOSE_WINDOW:
                data.getActionProcessor().handleCloseWindow();
                break;
            case PacketType.Client.USE_ENTITY:
                final WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(rawPacket);

                data.getCombatProcessor().handleUseEntity(useEntity);
                break;
            case PacketType.Client.POSITION:
            case PacketType.Client.POSITION_LOOK:
                final WrappedPacketInFlying pos = new WrappedPacketInFlying(rawPacket);

                data.getPositionProcessor().handle(pos.getX(), pos.getY(), pos.getZ(), pos.isOnGround());

                final WrappedPacketInFlying rotat = new WrappedPacketInFlying(rawPacket);

                data.getRotationProcessor().handle(rotat.getYaw(), rotat.getPitch());

                break;
            case PacketType.Client.LOOK:
                final WrappedPacketInFlying rot = new WrappedPacketInFlying(rawPacket);

                data.getRotationProcessor().handle(rot.getYaw(), rot.getPitch());
                break;
            case PacketType.Client.ARM_ANIMATION:
                data.getClickProcessor().handleArmAnimation();

                data.getCombatProcessor().handleArmAnimation();
                break;
            case PacketType.Client.TRANSACTION:
                final WrappedPacketInTransaction transaction = new WrappedPacketInTransaction(rawPacket);

                data.getVelocityProcessor().handleTransaction(transaction);
                break;
        }

        data.getChecks().forEach(check -> check.handle(packet));
    }
}