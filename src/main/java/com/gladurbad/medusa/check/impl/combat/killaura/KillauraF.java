package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.RaycastUtils;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;


@CheckInfo(name = "Killaura", type = "F", dev = true)
public class KillauraF extends Check {

    private final static ConfigValue swings = new ConfigValue(ConfigValue.ValueType.INTEGER, "swings");
    private static final ConfigValue minFlag = new ConfigValue(ConfigValue.ValueType.INTEGER, "min-flag");

    private int misses, potential;

    public KillauraF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving()) {
            if(packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                ++misses;
                if (RaycastUtils.getLookingEntity(data.getPlayer()) != null)
                    ++potential;
                if(misses >= swings.getInt()) {
                    if(potential > minFlag.getInt()) fail();
                    misses = 0;
                    potential = 0;
                }
            } else if(packet.getPacketId() == PacketType.Client.USE_ENTITY) {
                WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
                //if(wrappedPacketInUseEntity.getEntity().getType() == EntityType.PLAYER) {
                    --misses;
                if (RaycastUtils.getLookingEntity(data.getPlayer()) != null)
                    --potential;
                //}
            }
        }
    }
}
