package com.gladurbad.medusa.listener;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.util.anticheat.AlertUtil;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PlayerEjectEvent;
import io.github.retrooper.packetevents.event.impl.PostPlayerInjectEvent;

public class JoinQuitListener implements PacketListener {

    @PacketHandler
    public void onPlayerJoin(final PostPlayerInjectEvent event) {
        Medusa.INSTANCE.getPlayerDataManager().add(event.getPlayer());

        if (event.getPlayer().hasPermission("medusa.alerts")) {
            AlertUtil.toggleAlerts(Medusa.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()));
        }
    }

    @PacketHandler
    public void onPlayerQuit(final PlayerEjectEvent event) {
        Medusa.INSTANCE.getPlayerDataManager().remove(event.getPlayer());
    }
}
