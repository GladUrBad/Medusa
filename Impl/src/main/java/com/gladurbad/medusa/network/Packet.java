package com.gladurbad.medusa.network;

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

    public boolean isReceiving() { return direction == Direction.RECEIVE; }

    public boolean isSending() { return direction == Direction.SEND; }

    public enum Direction { SEND, RECEIVE }
}
