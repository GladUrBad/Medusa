package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created on 11/10/2020 Package com.gladurbad.medusa.check.impl.player.hand by GladUrBad
 */


@CheckInfo(name = "Hand (B)", experimental = true, description = "Checks for face occlusion when placing blocks.")
public class HandB extends Check implements Listener {

    public HandB(PlayerData data) {
        super(data);
        Bukkit.getPluginManager().registerEvents(this, Medusa.INSTANCE.getPlugin());
    }

    @Override
    public void handle(Packet packet) {
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer() == data.getPlayer()) {
            final BlockFace blockFace = event.getBlockAgainst().getFace(event.getBlock());
            final Location blockLoc = event.getBlockAgainst().getLocation();
            if (!interactedCorrectly(blockLoc, data.getPlayer().getEyeLocation(), blockFace)) {
                fail("face=" + blockFace);
            }
        }
    }

    //I know it's ugly. idc lol
    private boolean interactedCorrectly(Location blockLoc, Location playerLoc, BlockFace face) {
        if (face == BlockFace.UP) {
            final double limit = blockLoc.getY();
            return playerLoc.getY() > limit;
        } else if (face == BlockFace.DOWN) {
            final double limit = blockLoc.getY();
            return playerLoc.getY() < limit;
        } else if (face == BlockFace.WEST) {
            final double limit = blockLoc.getX();
            return limit > playerLoc.getX();
        } else if (face == BlockFace.EAST) {
            final double limit = blockLoc.getX();
            return playerLoc.getX() > limit;
        } else if (face == BlockFace.NORTH) {
            final double limit = blockLoc.getZ();
            return playerLoc.getZ() < limit;
        } else if (face == BlockFace.SOUTH) {
            final double limit = blockLoc.getZ();
            return playerLoc.getZ() > limit;
        } else {
            return true;
        }
    }
}
