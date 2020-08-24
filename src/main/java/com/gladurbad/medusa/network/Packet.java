package com.gladurbad.medusa.network;

import lombok.Getter;

@Getter
public class Packet {

    private Direction direction;
    private Object rawPacket;
    private byte packetId;

    public Packet(Direction direction, Object rawPacket, byte packetId) {
        this.direction = direction;
        this.rawPacket = rawPacket;
        this.packetId = packetId;
    }

    public boolean isReceiving() { return direction == Direction.RECEIVE; }

    public boolean isSending() { return direction == Direction.SEND; }

    public enum Direction { SEND, RECEIVE }
}
