package com.gladurbad.antimovehack.util;

import com.gladurbad.antimovehack.manager.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class ChatUtil {
    public static String color(String in) { return ChatColor.translateAlternateColorCodes('&', in); }
    public static final String antiMoveHackPrefix = color("&8[&2AntiMoveHack&8] &c");
    public static final String antiMoveHackSpacer = "                              ";

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
