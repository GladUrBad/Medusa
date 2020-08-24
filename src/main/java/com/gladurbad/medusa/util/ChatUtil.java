package com.gladurbad.medusa.util;

import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class ChatUtil {
    public static String color(String in) { return ChatColor.translateAlternateColorCodes('&', in); }
    public static final String medusaPrefix = color("&8[&2Medusa&8] &c");
    public static final String medusaSpacer = "                              ";

    public static void sendVerbose(String message) {
        PlayerDataManager.getPlayerData().values().stream().forEach(playerData -> playerData.getPlayer().sendMessage(ChatUtil.color(message)));
    }

    public static void sendVerbose(TextComponent message) {
        PlayerDataManager.getPlayerData().values().stream().filter(PlayerData::isAlerts).forEach(playerData -> playerData.getPlayer().spigot().sendMessage(message));
    }

    public static void sendAlert(String message) {
        PlayerDataManager.getPlayerData().values().stream().forEach(playerData -> playerData.getPlayer().sendMessage(ChatUtil.color(message)));
    }

    public static void sendAlert(TextComponent message) {
        PlayerDataManager.getPlayerData().values().stream().forEach(playerData -> playerData.getPlayer().spigot().sendMessage(message));
    }

}
