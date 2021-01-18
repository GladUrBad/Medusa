package com.gladurbad.medusa.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PlayerUtil {

    public ClientVersion getClientVersion(final Player player) {
        return PacketEvents.getAPI().getPlayerUtils().getClientVersion(player);
    }

    public int getPing(final Player player) {
        return  PacketEvents.getAPI().getPlayerUtils().getPing(player);
    }

    public int getDepthStriderLevel(final Player player) {
        if (player.getInventory().getBoots() != null && !ServerUtil.isLowerThan1_8()) {
            return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
        }
        return 0;
    }

    public double getBaseSpeed(Player player) {
        return 0.36 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseGroundSpeed(Player player) {
        return 0.288 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    /**
     * Gets the block in the given distance, If the block is null we immediately break to avoid issues
     *
     * @param player The player
     * @param distance The distance
     * @return The block in the given distance, otherwise null
     */
    public static Block getLookingBlock(final Player player, final int distance) {
        Location loc = player.getEyeLocation();

        final Vector v = loc.getDirection().normalize();

        for (int i = 1; i <= distance; i++) {
            loc.add(v);

            Block b = loc.getBlock();
            /*
            I'd recommend moving fiona's getBlock method in a seperate class
            So we can use it in instances like this.
             */
            if (b == null) break;

            if (b.getType() != Material.AIR) return b;
        }

        return null;
    }

    /**
     * Bukkit's getNearbyEntities method looks for all entities in all chunks
     * This is a lighter method and can also be used Asynchronously since we won't load any chunks
     *
     * @param location The location to scan for nearby entities
     * @param radius   The radius to expand
     * @return The entities within that radius
     * @author Nik
     */
    public static List<Entity> getEntitiesWithinRadius(final Location location, final double radius) {

        final double expander = 16.0D;

        final double x = location.getX();
        final double z = location.getZ();

        final int minX = (int) Math.floor((x - radius) / expander);
        final int maxX = (int) Math.floor((x + radius) / expander);

        final int minZ = (int) Math.floor((z - radius) / expander);
        final int maxZ = (int) Math.floor((z + radius) / expander);

        final World world = location.getWorld();

        List<Entity> entities = new ArrayList<>();

        for (int xVal = minX; xVal <= maxX; xVal++) {
            for (int zVal = minZ; zVal <= maxZ; zVal++) {
                if (world.isChunkLoaded(xVal, zVal)) {
                    entities.addAll(Arrays.asList(world.getChunkAt(xVal, zVal).getEntities()));
                }
            }
        }

        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);

        return entities;
    }

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

}
