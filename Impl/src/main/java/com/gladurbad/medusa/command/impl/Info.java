package com.gladurbad.medusa.command.impl;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.command.CommandInfo;
import com.gladurbad.medusa.command.MedusaCommand;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.util.ColorUtil;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "info", syntax = "<player>", purpose = "Returns information about the players client.")
public class Info extends MedusaCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = Medusa.INSTANCE.getPlayerDataManager().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Information for &c" + playerData.getPlayer().getName() + Config.ACCENT_ONE + "."));
                    sendRetardedNewLine(sender);
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_TWO + "&oGeneral information:"));
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Latency → " + Config.ACCENT_TWO + PacketEvents.getAPI().getPlayerUtils().getPing(playerData.getPlayer())) + "ms");
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Checks amount → " + Config.ACCENT_TWO + playerData.getChecks().size()));
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Sensitivity → " + Config.ACCENT_TWO + playerData.getRotationProcessor().getSensitivity() + "%"));
                    final String clientBrand = playerData.getClientBrand() == null ? "&cCould not resolve client brand for this player." : playerData.getClientBrand();
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE  + "Client brand: → " + Config.ACCENT_TWO + clientBrand));
                    sendRetardedNewLine(sender);
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_TWO + "&oViolations information:"));
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Total check violations → " + Config.ACCENT_TWO + playerData.getTotalViolations()));
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Combat check violations → " + Config.ACCENT_TWO + playerData.getCombatViolations()));
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Movement check violations → " + Config.ACCENT_TWO + playerData.getMovementViolations()));
                    sendMessage(sender, ColorUtil.translate(Config.ACCENT_ONE + "Player check violations → " + Config.ACCENT_TWO + playerData.getPlayerViolations()));
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}
