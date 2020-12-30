package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

/**
 * Created on 11/10/2020 Package com.gladurbad.medusa.check.impl.player.hand by GladUrBad
 */

@CheckInfo(name = "Hand (A)", experimental = true, description = "Checks for block interaction reach.")
public class HandA extends Check implements Listener {

    public HandA(PlayerData data) {
        super(data);
        Bukkit.getPluginManager().registerEvents(this, Medusa.INSTANCE.getPlugin());
    }

    @Override
    public void handle(Packet packet) {
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer() == data.getPlayer()) {
            final Vector vec = event.getBlock().getLocation().toVector().setY(0);
            final Vector playerVec = data.getPlayer().getLocation().toVector().setY(0);
            final double distance = playerVec.distance(vec) - 0.5;

            if (distance > 4.5) {
                fail(String.format("dist=%.2f", distance));
            }
        }
    }
}
