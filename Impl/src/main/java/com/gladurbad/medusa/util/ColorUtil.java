package com.gladurbad.medusa.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public final class ColorUtil {

    public String translate(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
