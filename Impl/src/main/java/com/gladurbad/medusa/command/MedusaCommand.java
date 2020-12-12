package com.gladurbad.medusa.command;

import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class MedusaCommand implements Comparable<MedusaCommand> {

    protected abstract boolean handle(final CommandSender sender, final Command command, final String label, final String[] args);

    public void sendLineBreak(final CommandSender sender) {
        sender.sendMessage(ColorUtil.translate(Config.ACCENT_TWO + "&m----------------------------------------------"));
    }

    public void sendRetardedNewLine(final CommandSender sender) {
        sender.sendMessage("");
    }

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }

    public CommandInfo getCommandInfo() {
        if (this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return this.getClass().getAnnotation(CommandInfo.class);
        } else {
            System.err.println("CommandInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    @Override
    public int compareTo(MedusaCommand o) {
        return 0;
    }
}
