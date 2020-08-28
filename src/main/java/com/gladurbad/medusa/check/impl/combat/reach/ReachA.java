package com.gladurbad.medusa.check.impl.combat.reach;

import com.gladurbad.medusa.Config;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.customtype.EvictingList;

import com.gladurbad.medusa.util.customtype.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

import net.minecraft.server.v1_8_R3.MathHelper;

import org.bukkit.Location;
import org.bukkit.entity.Entity;


@CheckInfo(name = "Reach", type = "A", dev = true)
public class ReachA extends Check {

    private final int REACH_BUFFER = (5 - Config.REACH_SENSITIVITY) * 2;

    private Entity attacked, lastAttacked;
    private final EvictingList<Pair<Long, Location>> historyLocations = new EvictingList<>(20);

    public ReachA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.getPacketId() == PacketType.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
            attacked = wrappedPacketInUseEntity.getEntity();
            if(historyLocations.size() == 20) {
                final long ping = PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer());
                if(ping < Config.REACH_MAXLATENCY) {
                    final double distance = this.historyLocations.stream()
                            .filter(pair -> Math.abs(now() - pair.getX()) < (Math.max(ping, 150L)))
                            .mapToDouble(pair -> {
                                Location victimLoc = pair.getY();
                                Location playerLoc = data.getLocation();

                                return playerLoc.toVector().setY(0).distance(victimLoc.toVector().setY(0)) - 0.4D;
                            }).min().orElse(0.0);

                    if (distance > Config.MAX_REACH) {
                        increaseBuffer();
                        if (buffer > REACH_BUFFER) fail();
                    } else {
                        decreaseBuffer();
                    }
                }
            }
            lastAttacked = attacked;

        } else if(isFlyingPacket(packet)) {
            if(attacked != null && lastAttacked != null) {
                historyLocations.add(new com.gladurbad.medusa.util.customtype.Pair<>(now(), attacked.getLocation()));
            }
        }
    }
}
