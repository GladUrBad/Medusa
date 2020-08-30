package com.gladurbad.medusa.util;

import com.gladurbad.medusa.Config;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class ChatUtil {
    public static String color(String in) { return ChatColor.translateAlternateColorCodes('&', in); }
    public static final String medusaPrefix = Config.PREFIX;
    public static final String medusaSpacer = "                              ";


    public static void sendVerbose(TextComponent message) {
        PlayerDataManager.getInstance().getPlayerData().values().stream().filter(PlayerData::isAlerts).forEach(playerData -> playerData.getPlayer().spigot().sendMessage(message));
    }
}
