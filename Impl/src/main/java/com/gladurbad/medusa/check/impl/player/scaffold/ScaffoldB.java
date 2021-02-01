package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Scaffold (B)", description = "Checks for tower hacks.", experimental = true)
public final class ScaffoldB extends Check {

    private int movements;
    private int lastX, lastY, lastZ;
    public ScaffoldB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            if (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) > 0) return;
            if (data.getPositionProcessor().getDeltaY() <= 0) return;

            if (!(wrapper.getX() == 1 && wrapper.getY() == 1 && wrapper.getZ() != 1)) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    if (lastX == wrapper.getX() && wrapper.getY() > lastY && lastZ == wrapper.getZ()) {
                        if (movements < 7) {
                            if (++buffer > 2) {
                                fail("ticks=" + movements);
                            }
                        } else {
                            buffer = 0;
                        }
                        movements = 0;
                    }
                    lastX = wrapper.getX();
                    lastY = wrapper.getY();
                    lastZ = wrapper.getZ();
                }
            }
        } else if (packet.isFlying()) {
            ++movements;
        }
    }
}
