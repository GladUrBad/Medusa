package com.gladurbad.medusa.packet;

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

    public boolean isReceiving() {
        return direction == Direction.RECEIVE;
    }

    public boolean isSending() {
        return direction == Direction.SEND;
    }

    public boolean isFlying() {
        return isReceiving() && PacketType.Client.Util.isInstanceOfFlying(packetId);
    }

    public boolean isUseEntity() {
        return isReceiving() && packetId == PacketType.Client.USE_ENTITY;
    }

    public boolean isExplosion() {
        return isReceiving() && packetId == PacketType.Server.EXPLOSION;
    }

    public boolean isRotation() {
        return isReceiving() && (packetId == PacketType.Client.LOOK || packetId == PacketType.Client.POSITION_LOOK);
    }

    public boolean isPosition() {
        return isReceiving() && (packetId == PacketType.Client.POSITION || packetId == PacketType.Client.POSITION_LOOK);
    }

    public boolean isArmAnimation() {
        return isReceiving() && packetId == PacketType.Client.ARM_ANIMATION;
    }

    public boolean isAbilities() {
        return isReceiving() && packetId == PacketType.Client.ABILITIES;
    }

    public boolean isBlockPlace() {
        return isReceiving() && packetId == PacketType.Client.BLOCK_PLACE;
    }

    public boolean isBlockDig() {
        return isReceiving() && packetId == PacketType.Client.BLOCK_DIG;
    }

    public boolean isWindowClick() { return isReceiving() && packetId == PacketType.Client.WINDOW_CLICK; }

    public boolean isEntityAction() {
        return isReceiving() && packetId == PacketType.Client.ENTITY_ACTION;
    }

    public boolean isPosLook() {
        return isReceiving() && packetId == PacketType.Client.POSITION_LOOK;
    }

    public boolean isCloseWindow() { return isReceiving() && packetId == PacketType.Client.CLOSE_WINDOW; }

    public boolean isKeepAlive() { return isReceiving() && packetId == PacketType.Client.KEEP_ALIVE; }

    public boolean isSteerVehicle() {
        return isReceiving() && packetId == PacketType.Client.STEER_VEHICLE;
    }

    public boolean isHeldItemSlot() {
        return isReceiving() && packetId == PacketType.Client.HELD_ITEM_SLOT;
    }

    public boolean isClientCommand() {
        return isReceiving() && packetId == PacketType.Client.CLIENT_COMMAND;
    }

    public boolean isCustomPayload() { return isReceiving() && packetId == PacketType.Client.CUSTOM_PAYLOAD; }

    public boolean isIncomingTransaction () {
        return isReceiving() && packetId == PacketType.Client.TRANSACTION;
    }

    public boolean isSendingTransaction() {
        return isSending() && packetId == PacketType.Server.TRANSACTION;
    }


    public boolean isTeleport() { return isSending() && packetId == PacketType.Server.POSITION; }

    public boolean isVelocity() {
        return isSending() && packetId == PacketType.Server.ENTITY_VELOCITY;
    }

    public boolean isRelEntityMove() {
        return isSending() && packetId == PacketType.Server.REL_ENTITY_MOVE;
    }

    public enum Direction { SEND, RECEIVE }
}
