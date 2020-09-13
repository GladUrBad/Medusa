package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Autoclicker", type = "A")
public class AutoclickerA extends Check {

    private int ticks, cps;
    private static final ConfigValue maxCPS = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-cps");

    public AutoclickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isReceiving() && !data.isDigging()) {
            if (isFlyingPacket(packet)) {
                if (++ticks >= 20) {
                    if (cps > maxCPS.getInt()) {
                        fail();
                    }

                    ticks = 0;
                    cps = 0;
                }
            } else if (packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                ++cps;
            }
        }
    }
}
