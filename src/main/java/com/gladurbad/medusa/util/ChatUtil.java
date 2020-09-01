package com.gladurbad.medusa.util;

import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;

public class ChatUtil {
    public static boolean papi;
    public static String color(String in) { return ChatColor.translateAlternateColorCodes('&', in); }
    public static String papi(String in, OfflinePlayer player) {
        if (papi)
            return PlaceholderAPI.setPlaceholders(player, in);
        else
            return in;
    }
    public static final String medusaPrefix = Config.PREFIX;
    public static final String medusaSpacer = "                              ";


    public static void sendVerbose(TextComponent message) {
        PlayerDataManager.getInstance().getPlayerData().values().stream().filter(PlayerData::isAlerts).forEach(playerData -> playerData.getPlayer().spigot().sendMessage(message));
    }
}
