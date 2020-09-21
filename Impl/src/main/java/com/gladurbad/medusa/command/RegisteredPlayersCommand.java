package com.gladurbad.medusa.command;

import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.playerdata.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RegisteredPlayersCommand extends MedusaArgument{

    public RegisteredPlayersCommand() {
        super("registeredplayers", "Lists the registered players on Medusa", "");
    }

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(MedusaArgument.responsePrefix + MedusaArgument.medusaSeparator);
        sender.sendMessage(MedusaArgument.responsePrefix + "There are " + PlayerDataManager.getInstance().getPlayerData().size() + " checked player(s).");
        sender.sendMessage(MedusaArgument.responsePrefix);
        for (PlayerData playerData : PlayerDataManager.getInstance().getPlayerData().values()) {
            sender.sendMessage(MedusaArgument.responsePrefix + "- " + playerData.getPlayer().getName());
        }
        sender.sendMessage(MedusaArgument.responsePrefix + MedusaArgument.medusaSeparator);
        return true;
    }
}
