package com.gladurbad.antimovehack.command;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.manager.PlayerDataManager;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LogsCommand extends AntiMoveHackArgument{

    protected LogsCommand() {
        super("logs", "Returns check logs for the specified player", "<player>");
    }

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && args.length > 1) {
            if(!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) return false;
            final UUID targetPlayerUUID = Bukkit.getPlayer(args[1]).getUniqueId();
            if(targetPlayerUUID != null) {
                PlayerData playerData = PlayerDataManager.getPlayerData().get(targetPlayerUUID);
                sender.sendMessage(responsePrefix + antiMoveHackSeparator);
                for (Check check : playerData.getChecks()) {
                    if(check.getVl() > 0) {
                        sender.sendMessage(responsePrefix + check.getCheckInfo().name() + "  (" + check.getCheckInfo().type() + ") VL: " + check.getVl() + "VL");
                    }
                }
                sender.sendMessage(responsePrefix + antiMoveHackSeparator);
                return true;
            }
        }
        return false;
    }
}

