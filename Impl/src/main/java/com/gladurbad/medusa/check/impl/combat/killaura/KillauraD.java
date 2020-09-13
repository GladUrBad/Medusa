package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Killaura", type = "D")
public class KillauraD extends Check {

    private static final ConfigValue sampleSize = new ConfigValue(ConfigValue.ValueType.INTEGER, "sample-size");
    private static final ConfigValue maxAccuracy = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-accuracy");

    private int swings, hits;

    public KillauraD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            if (++swings >= sampleSize.getInt()) {
                if (hits > maxAccuracy.getInt()) {
                    fail();
                }
                swings = 0;
                hits = 0;
            }
        } else if (packet.getPacketId() == PacketType.Client.USE_ENTITY) {
            ++hits;
        }
    }
}
