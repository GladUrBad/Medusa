package com.gladurbad.medusa.command.impl;

import com.gladurbad.medusa.command.CommandInfo;
import com.gladurbad.medusa.command.MedusaCommand;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.manager.ThemeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


@CommandInfo(name = "theme", syntax = "<name>", purpose = "Changes the theme.")
public final class Theme extends MedusaCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            for (ThemeManager.Theme theme : ThemeManager.themes) {
               if (args[1].equalsIgnoreCase(theme.getName())) {
                   Config.ALERT_FORMAT = theme.getAlertFormat();
                   Config.ACCENT_ONE = "&" + theme.getAccentColours().get(0).getChar();
                   Config.ACCENT_TWO = "&" + theme.getAccentColours().get(1).getChar();

                   sendMessage(sender, Config.ACCENT_ONE + "Updated theme to &c" + theme.getName() + " theme.");
                   return true;
               }
            }
        } else if (args.length == 1) {
            sendRetardedNewLine(sender);
            sendMessage(sender, Config.ACCENT_ONE + "Themes for the anti-cheat:");
            sendRetardedNewLine(sender);
            for (ThemeManager.Theme theme : ThemeManager.themes) {
                sendMessage(sender, Config.ACCENT_ONE + theme.getName());
            }
            sendRetardedNewLine(sender);
            return true;
        }
        return false;
    }
}
