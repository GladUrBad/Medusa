package com.gladurbad.medusa.command;

import com.gladurbad.medusa.Medusa;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedusaCommands implements CommandExecutor {

    private final List<MedusaArgument> arguments;

    public MedusaCommands(Medusa medusa) {
        arguments = new ArrayList<>();
        //Register commands here--------
        arguments.add(new LogsCommand());
        arguments.add(new AlertsCommand());
        arguments.add(new RegisteredPlayersCommand());
        arguments.add(new ExemptCommand());
        //------------------------------
        Collections.sort(arguments);
        MedusaArgument.medusa = medusa;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player){
            if (commandSender.hasPermission("medusa.commands") || commandSender.isOp()){
                if (args.length > 0) {
                    for(MedusaArgument medusaArgument : arguments) {
                        String argName = medusaArgument.getName();
                        if (argName.equals(args[0])) {
                            if (!medusaArgument.handle(commandSender, command, s, args)) {
                                commandSender.sendMessage(MedusaArgument.responsePrefix + "Usage: /medusa " + medusaArgument.getName() + " " + medusaArgument.getSyntax());
                            }
                            return true;
                        }
                    }
                } else {
                    commandSender.sendMessage(MedusaArgument.responsePrefix + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    for(MedusaArgument medusaArgument : arguments) {
                        commandSender.sendMessage(MedusaArgument.responsePrefix + "/medusa " + medusaArgument.getName());
                    }
                    commandSender.sendMessage(MedusaArgument.responsePrefix + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return true;
                }
            }
        } else commandSender.sendMessage("You have to be a player to do that.");
        return false;
    }
}
