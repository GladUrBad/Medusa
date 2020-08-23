package com.gladurbad.antimovehack.command;

import com.gladurbad.antimovehack.AntiMoveHack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntiMoveHackCommands implements CommandExecutor {

    private final List<AntiMoveHackArgument> arguments;

    public AntiMoveHackCommands(AntiMoveHack antiMoveHack) {
        arguments = new ArrayList<>();
        //Register commands here--------
        arguments.add(new LogsCommand());
        arguments.add(new AlertsCommand());
        //------------------------------
        Collections.sort(arguments);
        AntiMoveHackArgument.antiMoveHack = antiMoveHack;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player){
            if (commandSender.hasPermission("antimovehack.commands") || commandSender.isOp()){
                if(args.length > 0) {
                    for(AntiMoveHackArgument antiMoveHackArgument : arguments) {
                        String argName = antiMoveHackArgument.getName();
                        if(argName.equals(args[0])) {
                            if(!antiMoveHackArgument.handle(commandSender, command, s, args)) {
                                commandSender.sendMessage(AntiMoveHackArgument.responsePrefix + "Usage: /antimovehack " + antiMoveHackArgument.getName() + " " + antiMoveHackArgument.getSyntax());
                            }
                            return true;
                        }
                    }
                } else {
                    commandSender.sendMessage(AntiMoveHackArgument.responsePrefix + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    for(AntiMoveHackArgument antiMoveHackArgument : arguments) {
                        commandSender.sendMessage(AntiMoveHackArgument.responsePrefix + "/antimovehack " + antiMoveHackArgument.getName());
                    }
                    commandSender.sendMessage(AntiMoveHackArgument.responsePrefix + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return true;
                }
            }
        } else commandSender.sendMessage("You have to be a player to do that.");
        return false;
    }
}
