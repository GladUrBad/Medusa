package com.gladurbad.medusa.command;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.util.ChatUtil;
import lombok.Getter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Getter
public abstract class MedusaArgument implements Comparable<MedusaArgument>{

    private final String name, description, syntax;
    static Medusa medusa;
    static final String responsePrefix = ChatUtil.medusaPrefix + " ";
    static final String medusaSpacer = "                              ";
    static final String medusaSeparator = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    protected MedusaArgument(String name, String description, String syntax) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
    }

    protected abstract boolean handle(CommandSender sender, Command command, String label, String[] args);

    @Override
    public int compareTo(MedusaArgument o) {
        return 0;
    }
}
