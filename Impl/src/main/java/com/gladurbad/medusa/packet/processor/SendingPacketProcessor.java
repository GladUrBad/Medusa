package com.gladurbad.medusa.packet.processor;

import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.explosion.WrappedPacketOutExplosion;

public class SendingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {

        final Object rawPacket = packet.getRawPacket();

        switch (packet.getPacketId()) {
            case PacketType.Server.ENTITY_VELOCITY:
                final WrappedPacketOutEntityVelocity velocity = new WrappedPacketOutEntityVelocity(rawPacket);
                if (velocity.getEntity() == data.getPlayer()) {
                    data.getVelocityProcessor().handle(velocity.getVelocityX(), velocity.getVelocityY(), velocity.getVelocityZ());
                }
                break;
            case PacketType.Server.EXPLOSION:
                final WrappedPacketOutExplosion explosion = new WrappedPacketOutExplosion(rawPacket);
                data.getVelocityProcessor().handle(explosion.getPlayerMotionX(), explosion.getPlayerMotionY(), explosion.getPlayerMotionZ());
                break;
            case PacketType.Server.POSITION:
                data.getPositionProcessor().handleTeleport();
                break;
        }
        data.getChecks().forEach(check -> check.handle(packet));
    }
}
