package com.gladurbad.medusa.data.processor;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class VelocityProcessor {

    private final PlayerData data;
    private double velocityX, velocityY, velocityZ;
    private double lastVelocityX, lastVelocityY, lastVelocityZ;
    private int maxVelocityTicks, velocityTicks, ticksSinceVelocity;
    private short transactionID, velocityID;
    private long transactionPing, transactionReply;
    private boolean verifyingVelocity;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        this.ticksSinceVelocity = 0;

        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.velocityID = (short) ThreadLocalRandom.current().nextInt(32767);
        this.verifyingVelocity = true;
        PacketEvents.getAPI().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, velocityID, false));
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
            PacketEvents.getAPI().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, transactionID, false));
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
