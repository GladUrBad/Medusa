package com.gladurbad.medusa.command;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExemptCommand extends MedusaArgument{

    public ExemptCommand() {
        super("exempt", "Toggles the ability to bypass checks for the specified player.", "<player>");
    }

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 2) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    PlayerData playerData = Medusa.getInstance().getDataManager().getPlayerData(Bukkit.getPlayer(args[1]));
                    if (playerData != null) {
                        if (playerData.isShouldCheck()) {
                            playerData.setShouldCheck(false);
                            sender.sendMessage(MedusaArgument.responsePrefix + playerData.getPlayer().getName() + " is now bypassing.");
                        } else {
                            playerData.setShouldCheck(true);
                            sender.sendMessage(MedusaArgument.responsePrefix + playerData.getPlayer().getName() + " is no longer bypassing.");
                        }
                        return true;
                    }
                } else {
                    sender.sendMessage(MedusaArgument.responsePrefix + "\"" + args[1] + "\"" + " could not be found.");
                    return true;
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
        }
        return false;
    }
}
