package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.util.Vector;

@CheckInfo(name = "Velocity (B)", description = "Checks for horizontal velocity.", experimental = true)
public class VelocityB extends Check {

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
    }
}
