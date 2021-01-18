package com.gladurbad.medusa.util;

import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PlayerUtil {

    public ClientVersion getClientVersion(final Player player) {
        return PacketEvents.get().getPlayerUtils().getClientVersion(player);
    }

    public int getPing(final Player player) {
        return  PacketEvents.get().getPlayerUtils().getPing(player);
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

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

    public boolean shouldCheckJesus(final PlayerData data) {
        final boolean onWater = data.getPositionProcessor().isCollidingAtLocation(
                -0.1,
                material -> material.toString().contains("WATER"),
                PositionProcessor.CollisionType.ANY
        );

        final boolean hasEdgeCases = data.getPositionProcessor().isCollidingAtLocation(
                -0.001,
                material -> material == Material.CARPET || material == Material.WATER_LILY,
                PositionProcessor.CollisionType.ANY
        );

        return onWater && !hasEdgeCases;
    }

}
