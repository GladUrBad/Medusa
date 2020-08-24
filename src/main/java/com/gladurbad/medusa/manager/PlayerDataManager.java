package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.playerdata.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    @Getter
    private static Map<UUID, PlayerData> playerData = new HashMap<>();

    public static boolean containsPlayer(Player player) {
        return containsPlayer(player.getUniqueId());
    }

    public static boolean containsPlayer(UUID playerUUID) {
        return playerData.containsKey(playerUUID);
    }

}
