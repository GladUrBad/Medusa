package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.ChatUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.core.helpers.Strings;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertManager {

    public static void verbose(PlayerData data, Check check) {
        if(check.getVl() > Config.VL_TO_ALERT) {
            TextComponent alertMessage = new TextComponent(ChatUtil.papi(ChatUtil.color(Config.ALERT_FORMAT), data.getPlayer())
              .replace("%prefix%", Config.PREFIX)
              .replace("%player%", data.getPlayer().getName())
              .replace("%check%", check.getCheckInfo().name())
              .replace("%type%", check.getCheckInfo().type())
              .replace("%dev%", check.getCheckInfo().dev() ? ChatUtil.color("&c(Dev)&r") : "")
              .replace("%vl%", "" + check.getVl()));
            alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
            alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.color("&cClick to teleport.")).create()));

            ChatUtil.sendVerbose(alertMessage);
        }

        if (!Config.TESTMODE && data.getPlayer().isOnline()) {
            String type = Config.getType(check.getCheckInfo().name());
            List<Check> checks = data.getTypes().get(check.getCheckInfo().name());
            AtomicInteger vl = new AtomicInteger();
            checks.forEach(c -> vl.addAndGet((int) (c.getVl() * c.getVlAdd().getDouble())));
            int prevVl = data.getPrevVL().get(check.getCheckInfo().name());
            List<String> commands = Config.PUNISH_COMMANDS.get(type);
            for (int i = vl.get() + 1; i <= prevVl; i++) {
                String cmd = commands.get(i);
                if (cmd != null && cmd.trim().isEmpty())
                    Bukkit.getScheduler().runTask(Medusa.getInstance(), () ->
                      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatUtil.papi(ChatUtil.color(cmd), data.getPlayer())
                        .replace("%prefix%", Config.PREFIX)
                        .replace("%player%", data.getPlayer().getName())
                        .replace("%check%", check.getCheckInfo().name())
                        .replace("%type%", check.getCheckInfo().type())
                        .replace("%dev%", check.getCheckInfo().dev() ? ChatUtil.color("&c(Dev)&r") : "")
                        .replace("%vl%", "" + check.getVl())));
            }
            data.getPrevVL().put(check.getCheckInfo().name(), vl.get());
        }
        /*if(check.getVl() > check.getMaxVL() && !Config.TESTMODE && data.getPlayer().isOnline()) {
            if(!check.getPunishCommand().isEmpty()) {
                Bukkit.getScheduler().runTask(Medusa.getInstance(), () ->
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), check.getPunishCommand()
                        .replace("%player%", data.getPlayer().getName())
                        .replace("%prefix%", Config.PREFIX)));
            }
        } //broken rn.*/
    }
}
