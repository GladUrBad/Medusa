package com.gladurbad.medusa.data.processor;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import lombok.Getter;
import com.gladurbad.medusa.data.PlayerData;
import org.bukkit.entity.Entity;


@Getter
public class CombatProcessor {

    private final PlayerData data;
    private int hitTicks, swings, hits, currentTargets;
    private double hitMissRatio, distance;
    private Entity target, lastTarget;

    public CombatProcessor(PlayerData data) {
        this.data = data;
    }

    public void handleUseEntity(final WrappedPacketInUseEntity wrapper) {
        if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            return;
        }

        if (wrapper.getEntity() != null) {
            lastTarget = target == null ? wrapper.getEntity() : target;
            target = wrapper.getEntity();

            distance = data.getPlayer().getLocation().toVector().setY(0).distance(target.getLocation().toVector().setY(0)) - .42;

            ++hits;

            hitTicks = 0;

            if (target != lastTarget) {
                ++currentTargets;
            }
        }
    }

    public void handleArmAnimation() {
        ++swings;
    }

    public void handleFlying() {
        ++hitTicks;
        currentTargets = 0;

        if (swings > 1) {
            hitMissRatio = ((double) hits / (double) swings) * 100;
        }
        if (hits > 100 || swings > 100) {
            hits = swings = 0;
        }
    }
}
