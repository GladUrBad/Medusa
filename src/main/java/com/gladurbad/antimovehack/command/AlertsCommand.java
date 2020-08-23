package com.gladurbad.antimovehack.command;

import com.gladurbad.antimovehack.manager.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand extends AntiMoveHackArgument{

    protected AlertsCommand() {
        super("alerts", "Toggles alerts on/off", "");
    }

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            PlayerData playerData = PlayerDataManager.getPlayerData().get(((Player) sender).getUniqueId());
            if (playerData.isAlerts()) {
                playerData.setAlerts(false);
                sender.sendMessage(AntiMoveHackArgument.responsePrefix + "Simple alerts toggled off.");
                return true;
            } else {
                playerData.setAlerts(true);
                sender.sendMessage(AntiMoveHackArgument.responsePrefix + "Simple alerts toggled on.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
        }
        return false;
    }
}
