package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Created on 10/26/2020 Package com.gladurbad.medusa.check.impl.combat.reach by GladUrBad
 *
 * This check ensures that the attack distance is not too far. In Minecraft, the legit range is 3.0 blocks.
 * Some mods change this range, like Fast Math from Optifine or Badlion Client, however neither should false flag
 * this check. This check uses a rewind time system, in order to compensate for high latency. In testing, I have
 * found that this check can somewhat reliably detect 3.2-3.3 blocks of reach.
 *
 * Sort of taken from Frequency, however I use a different distance calculation for it.
 */

@CheckInfo(name = "HitBox (A)", description = "Checks for attacking distance.")
public class HitBoxA extends Check {

    public HitBoxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK ||
                data.getPlayer().getGameMode() != GameMode.SURVIVAL ||
                data.getCombatProcessor().getTarget() != data.getCombatProcessor().getLastTarget()) return;

            final int ticks = Medusa.INSTANCE.getTickManager().getTicks();
            final int latencyTicks = NumberConversions.floor(PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer()) / 50.) + 3;

            final Vector playerLocAsVector = data.getPlayer().getLocation().toVector().setY(0);
            if (data.getTargetLocations().isFull()) {
                final double distance = data.getTargetLocations().stream()
                        .filter(pair -> Math.abs(ticks - pair.getY() - latencyTicks) < 3)
                        .mapToDouble(pair -> {
                            Location location = pair.getX();
                            return location.toVector().setY(0).distance(playerLocAsVector) - 0.4D;
                        }).min().orElse(-1);

                if (distance > 3.1) {
                    if (increaseBuffer() > 4) {
                        fail("reach=" + distance + " buffer=" + getBuffer());
                    }
                } else {
                    decreaseBufferBy(0.03);
                }
            }
        }
    }

}