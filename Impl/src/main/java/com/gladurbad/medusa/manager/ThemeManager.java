package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.config.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class ThemeManager {

    public static final List<Theme> themes = new ArrayList<>();

    //This is so jank, maybe I will rewrite it in the future.
    public static void setup() {
        for (String str : Config.THEMES) {
            final String[] themePieces = str.split("\\$ ");
            final String[] accentColourCodes = themePieces[2].split(" ");

            final List<ChatColor> accentColourList = new ArrayList<>();

            for (String string : accentColourCodes) {
                accentColourList.add(ChatColor.getByChar(string.charAt(1)));
            }
            final Theme theme = new Theme(themePieces[0], themePieces[1], accentColourList);
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
