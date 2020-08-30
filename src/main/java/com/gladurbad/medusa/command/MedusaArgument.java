package com.gladurbad.medusa.command;

import com.gladurbad.medusa.Config;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.util.ChatUtil;
import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Getter
@RequiredArgsConstructor
public abstract class MedusaArgument implements Comparable<MedusaArgument>{

    private final String name, description, syntax;
    static Medusa medusa;
    static final String responsePrefix = Config.COMMAND_PREFIX + " ";
    static final String medusaSpacer = "                              ";
    static final String medusaSeparator = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    protected abstract boolean handle(CommandSender sender, Command command, String label, String[] args);

    @Override
    public int compareTo(MedusaArgument o) {
        return 0;
    }
}
