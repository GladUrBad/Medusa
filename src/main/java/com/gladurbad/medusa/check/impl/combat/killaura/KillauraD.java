package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.packet.PacketType;

@CheckInfo(name = "Killaura", type = "D", dev = true)
public class KillauraD extends Check {

    private int hitTicks;
    private float lastDeltaYaw, lastDeltaPitch;

    public KillauraD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        //Thanks to Elevated for making this check. https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/aimassist/AimAssistE.java
        //He said he doesn't care if people use things in Frequency, as long as credit is given, so don't get on my ass about "un-rightfully skidding" this check.
        if(packet.isReceiving() && (packet.getPacketId() == PacketType.Client.LOOK || packet.getPacketId() == PacketType.Client.POSITION_LOOK)) {
            if(++hitTicks < 3) {
                final float deltaYaw = data.getDeltaYaw();
                final float deltaPitch = data.getDeltaPitch();

                // Grab the gcd using an expander.
                final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
                final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

                // Get the constant for both rotation updates by dividing by the expander
                final double constantYaw = divisorYaw / MathUtil.EXPANDER;
                final double constantPitch = divisorPitch / MathUtil.EXPANDER;

                // Get the estimated mouse delta from the constant
                final double currentX = deltaYaw / constantYaw;
                final double currentY = deltaPitch / constantPitch;

                // Get the estimated mouse delta from the old rotations using the new constant
                final double previousX = lastDeltaYaw / constantYaw;
                final double previousY = lastDeltaPitch / constantPitch;

                // Make sure the rotation is not very large and not equal to zero and get the modulo of the xys
                if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                    final double moduloX = currentX % previousX;
                    final double moduloY = currentY % previousY;

                    // Get the floor delta of the the moduloes
                    final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                    final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                    // Impossible to have a different constant in two rotations
                    final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                    final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                    if (invalidX && invalidY) {
                        increaseBuffer();
                        if (buffer > 3) {
                            fail();
                        }
                    } else {
                        decreaseBuffer();
                    }
                }
                this.lastDeltaYaw = deltaYaw;
                this.lastDeltaPitch = deltaPitch;
            }

        } else if(packet.isReceiving() && packet.getPacketId() == PacketType.Client.USE_ENTITY) {
            hitTicks = 0;
        }
    }
}
