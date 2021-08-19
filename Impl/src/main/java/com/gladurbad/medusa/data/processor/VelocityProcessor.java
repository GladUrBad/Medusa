package com.gladurbad.medusa.data.processor;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import io.github.retrooper.packetevents.utils.vector.Vector3d;
import lombok.Getter;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class VelocityProcessor {

    private final PlayerData data;
    private double velocityX, velocityY, velocityZ, velocityXZ, lastVelocityX, lastVelocityY, lastVelocityZ, lastVelocityXZ;
    private int maxVelocityTicks, velocityTicks, ticksSinceVelocity;
    private short transactionID, velocityID;
    private long transactionPing, transactionReply;
    private boolean verifyingVelocity;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final Vector3d velocity) {
        this.ticksSinceVelocity = 0;

        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;
        lastVelocityXZ = velocityXZ;

        this.velocityX = velocity.getX();
        this.velocityY = velocity.getY();
        this.velocityZ = velocity.getZ();
        velocityXZ = Math.hypot(velocityX, velocityZ);


        this.velocityID = (short) ThreadLocalRandom.current().nextInt(32767);
        this.verifyingVelocity = true;

        PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, velocityID, false));
    }

    public void handleTransaction(final WrappedPacketInTransaction wrapper) {

        if (this.verifyingVelocity && wrapper.getActionNumber() == this.velocityID) {
            this.verifyingVelocity = false;
            this.velocityTicks = Medusa.INSTANCE.getTickManager().getTicks();
            this.maxVelocityTicks = (int) (((lastVelocityZ + lastVelocityX) / 2 + 2) * 15);
        }

        if (wrapper.getActionNumber() == transactionID) {
            transactionPing = System.currentTimeMillis() - transactionReply;

            transactionID = (short) ThreadLocalRandom.current().nextInt(32767);
            PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, transactionID, false));
            transactionReply = System.currentTimeMillis();
        }
    }

    public void handleFlying() {
        ++ticksSinceVelocity;
    }

    public boolean isTakingVelocity() {
        return Math.abs(Medusa.INSTANCE.getTickManager().getTicks() - this.velocityTicks) < this.maxVelocityTicks;
    }
}
