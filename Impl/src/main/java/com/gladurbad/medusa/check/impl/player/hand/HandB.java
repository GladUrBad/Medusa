package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;

/**
 * Created by Spiriten.
 */

@CheckInfo(name = "Hand (B)", experimental = true, description = "Checks for face place tendencies.")
public final class HandB extends Check {

    private long lastDifference, lastPlaceMS;

    public HandB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {

            //calculate our difference in place times
            long difference = now() - lastPlaceMS;

            //check if we placed atleast 195 ms apart (since default difference is around 200, but we go a little
            //lower to prevent falses) and our difference is the same as previous
            if (difference < 195 && difference == lastDifference) {
                if (++buffer > 2.5) {
                    fail("d=" + difference + " ld=" + lastDifference + " b=" + buffer);
                }
            } else buffer = Math.max(buffer - 0.15, 0);

            //handle saving our needed longs
            lastDifference = difference;
            lastPlaceMS = now();
        }
    }
}