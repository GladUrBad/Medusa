package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.config.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class ThemeManager {

    public static final List<Theme> themes = new ArrayList<>();

    public static void setup() {
        for (String str : Config.THEMES) {
            final String message = Medusa.INSTANCE.getPlugin().getConfig().getString("appearance.themes." + str + ".message");
            final List<String> colors = Medusa.INSTANCE.getPlugin().getConfig().getStringList("appearance.themes." + str + ".colors");

            final List<ChatColor> accentColourList = new ArrayList<>();

            for (String string : colors) {
                accentColourList.add(ChatColor.getByChar(string.charAt(1)));
            }

            final Theme theme = new Theme(str, message, accentColourList);
            themes.add(theme);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Theme {
        private final String name;
        private final String alertFormat;
        private final List<ChatColor> accentColours;
    }
}
