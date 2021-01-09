package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.in.blockplace.WrappedPacketInBlockPlace;
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
        if (event.getPlayer() == player()) {
            final BlockFace face = event.getBlockAgainst().getFace(event.getBlock());
            final Location eyeLocation = player().getEyeLocation();
            final Location location = event.getBlockAgainst().getLocation();

            final boolean invalid = !interactedCorrectly(location, eyeLocation, face);

            if (invalid) {
                fail();
            }
        }
    }

    private boolean interactedCorrectly(Location block, Location player, BlockFace face) {
        switch (face) {
            case UP:
                return player.getY() > block.getY();
            case DOWN:
                return player.getY() < block.getY();
            case WEST:
                return player.getX() < block.getX();
            case EAST:
                return player.getX() > block.getX();
            case NORTH:
                return player.getZ() < block.getZ();
            case SOUTH:
                return player.getZ() > block.getZ();
            default:
                return true;
        }
    }
}
