package com.gladurbad.medusa.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ServerUtil {

    public double getTPS() {
        return PacketEvents.get().getServerUtils().getTPS();
    }

    public ServerVersion getServerVersion() {
        return PacketEvents.get().getServerUtils().getVersion();
    }

    public boolean isLowerThan1_8() {
        return getServerVersion().isLowerThan(ServerVersion.v_1_8);
    }

    public boolean isHigherThan1_13_2() {
        return getServerVersion().isHigherThan(ServerVersion.v_1_13_2);
    }
}
