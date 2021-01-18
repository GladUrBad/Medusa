package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.autoclicker by GladUrBad
 */

@CheckInfo(name = "AutoClicker (A)", description = "Checks for attack speed.")
public class AutoClickerA extends Check {

    private int ticks, cps;

    public AutoClickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.AUTO_CLICKER)) {
            if (++ticks >= 20) {
                if (cps > 24) {
                    fail("cps=" + cps);
                }
                ticks = cps = 0;
            }
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                ++cps;
            }
        }
    }
}
