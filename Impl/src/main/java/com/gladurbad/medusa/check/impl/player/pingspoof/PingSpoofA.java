package com.gladurbad.medusa.check.impl.player.pingspoof;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;

@CheckInfo(name = "PingSpoof", type = "A", dev = true)
public class PingSpoofA extends Check {

    private static final ConfigValue maxPingDifference = new ConfigValue(ConfigValue.ValueType.LONG, "max-ping-difference");
    private static final ConfigValue maxBuffer = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-buffer");

    public PingSpoofA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final long keepAlivePing = PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer());
            final long transactionPing = data.getTransactionPing();

            final long pingDifference = Math.abs(keepAlivePing - transactionPing);

            if (pingDifference > maxPingDifference.getLong()) {
                if (increaseBuffer() > maxBuffer.getDouble()) {
                    fail();
                    resetBuffer();
                }
            }
        }
    }
}
