package com.gladurbad.antimovehack.command;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.util.ChatUtil;
import lombok.Getter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Getter
public abstract class AntiMoveHackArgument implements Comparable<AntiMoveHackArgument>{

    private final String name, description, syntax;
    static AntiMoveHack antiMoveHack;
    static final String responsePrefix = ChatUtil.antiMoveHackPrefix + " ";
    static final String antiMoveHackSpacer = "                              ";
    static final String antiMoveHackSeparator = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    protected AntiMoveHackArgument(String name, String description, String syntax) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
    }

    protected abstract boolean handle(CommandSender sender, Command command, String label, String[] args);

    @Override
    public int compareTo(AntiMoveHackArgument o) {
        return 0;
    }
}
