package com.gladurbad.medusa.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.enums.ClientVersion;
import io.github.retrooper.packetevents.enums.ServerVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PlayerUtil {
    private PacketEventsAPI packetEventsAPI = PacketEvents.getAPI();

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

    public  float getBaseSpeed(Player player) {
        return 0.34f + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public ServerVersion getServerVersion(){
        return packetEventsAPI.getServerUtils().getVersion();
    }
}
