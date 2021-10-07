package com.gladurbad.medusa.command;

import com.gladurbad.medusa.MedusaPlugin;
import com.gladurbad.medusa.command.impl.*;
import com.gladurbad.medusa.util.ColorUtil;
import com.gladurbad.medusa.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandManager implements CommandExecutor {

    private final List<MedusaCommand> commands = new ArrayList<>();

    public CommandManager(final MedusaPlugin plugin) {
        commands.add(new Alerts());
        commands.add(new Info());
        commands.add(new Debug());
        commands.add(new Violations());
        commands.add(new Theme());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] args) {
        if (commandSender.hasPermission("medusa.commands")) {
            if (args.length > 0) {
                for (final MedusaCommand medusaCommand : commands) {
                    final String commandName = medusaCommand.getCommandInfo().name();
                    if (commandName.equals(args[0])) {
                        if (commandSender.hasPermission("medusa." + commandName)) {
                            if (!medusaCommand.handle(commandSender, command, string, args)) {
                                commandSender.sendMessage(ColorUtil.translate(Config.ACCENT_ONE + "Usage: /medusa " +
                                        medusaCommand.getCommandInfo().name() + " " +
                                        medusaCommand.getCommandInfo().syntax()));
                            }
                        } else {
                            return false;
                        }
                        return true;
                    }
                }
            } else {
                commandSender.sendMessage("");
                commandSender.sendMessage(ColorUtil.translate(Config.ACCENT_ONE + "Medusa AntiCheat Commands:\n" + " \n"));
                for (final MedusaCommand medusaCommand : commands) {
                    commandSender.sendMessage(ColorUtil.translate( Config.ACCENT_ONE + "/medusa " +
                            medusaCommand.getCommandInfo().name() + " " +
                            medusaCommand.getCommandInfo().syntax()));
                }
                commandSender.sendMessage("");
                return true;
            }
        }
        return false;
    }
}
