package com.gladurbad.medusa.check.impl.player.protocol;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Protocol (K)", description = "Checks for no set rotate cheats.")
public final class ProtocolK extends Check {

    public ProtocolK(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            //this check should only act when the player is being teleported.
            //it works by checking if the rotation packet is the same as what the positionOut packet was.
            if (data.getPositionProcessor().isTeleporting()) {
                //used to clean up code
                float teleportPitch = data.getRotationProcessor().getTeleportPitch(), pitch = data.getRotationProcessor().getPitch();
                float teleportYaw = data.getRotationProcessor().getYaw(), yaw = data.getRotationProcessor().getTeleportYaw();

                //check if the yaw sent by the client is the same as what was sent by the server
                if (teleportYaw != yaw) {
                    fail("yaw=" + yaw + " teleport-yaw=" + teleportYaw + " tst=" +
                            data.getPositionProcessor().getSinceTeleportTicks());
                }
                //if the server tells the client to set their pitch above 90, both negative and positive, dont run.
                //its impossible for the client to do that
                if (Math.abs(teleportPitch) > 90) return;
                //check if the pitch sent by the client is the same as what was sent by the server
                if (data.getRotationProcessor().getPitch() != data.getRotationProcessor().getTeleportPitch()) {
                    fail("pitch=" + pitch + " teleport-pitch=" + teleportPitch + " tst=" +
                            data.getPositionProcessor().getSinceTeleportTicks());
                }
            }
        }
    }
}
