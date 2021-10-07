package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.autoclicker by GladUrBad
 */

@CheckInfo(name = "AutoClicker (A)", description = "Checks for attack speed.")
public final class AutoClickerA extends Check {

    private int ticks, cps;
    private static final ConfigValue maxCps = new ConfigValue(ConfigValue.ValueType.INTEGER, "max-cps");

    public AutoClickerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (++ticks >= 20) {
                debug("cps=" + cps);
                if (cps > maxCps.getInt() && !isExempt(ExemptType.AUTO_CLICKER)) {
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
