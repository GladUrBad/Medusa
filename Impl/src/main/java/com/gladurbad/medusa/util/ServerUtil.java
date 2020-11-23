package com.gladurbad.medusa.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerUtil {

    public double getTPS() {
        return PacketEvents.getAPI().getServerUtils().getTPS();
    }

    public ServerVersion getServerVersion() {
        return PacketEvents.getAPI().getServerUtils().getVersion();
    }

    public boolean isLowerThan1_8() {
        return getServerVersion().isLowerThan(ServerVersion.v_1_8);
    }
}
