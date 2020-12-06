package com.gladurbad.medusa.check.impl.combat.hitbox;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.HitboxExpansion;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "HitBox (C)", experimental = true, description = "A basic verbose reach check.")
public class HitBoxC extends Check {

    public HitBoxC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final Vector attacker = data.getPlayer().getLocation().toVector().setY(0);
                final Vector victim = data.getCombatProcessor().getTarget().getLocation().toVector().setY(0);

                final double expansion = HitboxExpansion.getExpansion(data.getCombatProcessor().getTarget());
                final double distance = attacker.distance(victim) - expansion;

                if (distance > 3.5) {
                    if (increaseBuffer() > 15) {
                        if (getBuffer() > 20) setBuffer(20);
                        fail(String.format("reach=%.2f, buffer=%.2f", distance, getBuffer()));
                    }
                } else {
                    decreaseBuffer();
                }
            }
        }
    }
}
