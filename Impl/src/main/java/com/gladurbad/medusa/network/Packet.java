package com.gladurbad.medusa.network;

import io.github.retrooper.packetevents.packettype.PacketType;
import lombok.Getter;

@Getter
public class Packet {

    private final Direction direction;
    private final Object rawPacket;
    private final byte packetId;

    public Packet(Direction direction, Object rawPacket, byte packetId) {
        this.direction = direction;
        this.rawPacket = rawPacket;
        this.packetId = packetId;
    }

    //Direction checking.
    public boolean isReceiving() { return direction == Direction.RECEIVE; }

    public boolean isSending() { return direction == Direction.SEND; }

    //Convenient methods to use to check packets I use a lot.
    public boolean isFlying() {
        return isReceiving() && PacketType.Client.Util.isInstanceOfFlying(packetId);
    }

    public boolean isUseEntity() {
        return isReceiving() && packetId == PacketType.Client.USE_ENTITY;
    }

    public boolean isRotation() {
        return isReceiving() && (packetId == PacketType.Client.LOOK || packetId == PacketType.Client.POSITION_LOOK);
    }

    public boolean isSwing() {
        return isReceiving() && packetId == PacketType.Client.ARM_ANIMATION;
    }

    public boolean isPosition() {
        return isReceiving() && (packetId == PacketType.Client.POSITION || packetId == PacketType.Client.POSITION_LOOK);
    }


    public enum Direction { SEND, RECEIVE }
}
