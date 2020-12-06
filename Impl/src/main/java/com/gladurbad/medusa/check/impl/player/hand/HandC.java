package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Created on 11/10/2020 Package com.gladurbad.medusa.check.impl.player.hand by GladUrBad
 */


@CheckInfo(name = "Hand (C)", description = "Checks for valid block breaks.")
public class HandC extends Check implements Listener {

    public HandC(PlayerData data) {
        super(data);
        Bukkit.getPluginManager().registerEvents(this, Medusa.INSTANCE.getPlugin());
    }

    @Override
    public void handle(Packet packet) {
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer() == data.getPlayer()) {
            if (event.getBlock().isLiquid()) fail("block=" + event.getBlock().getType());
        }
    }
}
