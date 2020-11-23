package com.gladurbad.medusa.command.impl;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.command.MedusaCommand;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.command.CommandInfo;
import com.gladurbad.medusa.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "checks", syntax = "<player>", purpose = "Get the players registered checks.")
public class Checks extends MedusaCommand {

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = Medusa.INSTANCE.getPlayerDataManager().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "There are " + playerData.getChecks().size() + " registered checks for " + player.getName() + "\n" + " \n"));
                    for (final Check check : playerData.getChecks()) {
                        sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + check.getCheckInfo().name()));
                    }
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}
