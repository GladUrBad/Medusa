package com.gladurbad.medusa.command;

import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand extends MedusaArgument {

    protected AlertsCommand() {
        super("alerts", "Toggles alerts on/off", "");
    }

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerData((Player) sender);
            if (playerData.isAlerts()) {
                playerData.setAlerts(false);
                sender.sendMessage(MedusaArgument.responsePrefix + "Alerts toggled off.");
                return true;
            } else {
                playerData.setAlerts(true);
                sender.sendMessage(MedusaArgument.responsePrefix + "Alerts toggled on.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this com.gladurbad.medusa.command.");
        }
        return false;
    }
}
