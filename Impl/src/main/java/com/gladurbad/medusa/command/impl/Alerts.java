package com.gladurbad.medusa.command.impl;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.command.MedusaCommand;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.util.anticheat.AlertUtil;
import com.gladurbad.medusa.command.CommandInfo;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "alerts", purpose = "Toggles cheat alerts.")
public final class Alerts extends MedusaCommand {

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final PlayerData data = Medusa.INSTANCE.getPlayerDataManager().getPlayerData(player);

            if (data != null) {
                if (AlertUtil.toggleAlerts(data) == AlertUtil.ToggleAlertType.ADD) {
                    sendMessage(sender, Config.ACCENT_ONE + "Toggled your cheat alerts &2on" + Config.ACCENT_ONE + ".");
                } else {
                    sendMessage(sender, Config.ACCENT_ONE + "Toggled your cheat alerts &coff" + Config.ACCENT_ONE + ".");
                }
                return true;
            }
        } else {
            sendMessage(sender, "Only players can execute this command.");
        }
        return false;
    }
}
