package com.gladurbad.antimovehack.util;

import com.gladurbad.antimovehack.playerdata.PlayerData;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;


@UtilityClass
public class AlertUtils {
    public void handleViolation(PlayerData playerData, String info, int violationLevel, Location setBackLocation) {
        if(setBackLocation != null) {
            playerData.getPlayer().teleport(setBackLocation);
        }

        TextComponent alertMessage = new TextComponent
                (ChatColor.translateAlternateColorCodes('&', "&8[&2AntiMoveHack&8] &a%player% &2- &a%info% &8[&cVL: %vl%&8]")
                .replace("%player%", playerData.getPlayer().getName())
                .replace("%info%", info)
                .replace("%vl%", Integer.toString(violationLevel)));
        alertMessage.setHoverEvent(new HoverEvent
                (HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&cClick to teleport")).create()));

        playerData.getPlayer().spigot().sendMessage(alertMessage);
    }
}
