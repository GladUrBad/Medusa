package com.gladurbad.medusa.util.anticheat;

import com.gladurbad.api.listener.MedusaFlagEvent;
import com.gladurbad.api.listener.MedusaSendAlertEvent;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.util.ColorUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import com.gladurbad.medusa.util.ServerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import com.gladurbad.medusa.config.Config;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

@Getter
@UtilityClass
public final class AlertUtil {

    private final Set<PlayerData> alerts = new HashSet<>();

    public ToggleAlertType toggleAlerts(final PlayerData data) {
        if (alerts.contains(data)) {
            alerts.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            alerts.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public void handleAlert(final Check check, final PlayerData data, final String info) {
        final TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%uuid%", data.getPlayer().getUniqueId().toString())
                .replaceAll("%checkName%", check.getJustTheName())
                .replaceAll("%ping%", Integer.toString(PlayerUtil.getPing(data.getPlayer())))
                .replaceAll("%checkType%", Character.toString(check.getType()))
                .replaceAll("%dev%", check.getCheckInfo().experimental() ? ColorUtil.translate("&7*") : "")
                .replaceAll("%vl%", Integer.toString(check.getVl()))
                .replaceAll("%maxvl%", Integer.toString(check.getMaxVl()))
                .replaceAll("%ping%", Integer.toString(PlayerUtil.getPing(data.getPlayer())))
                .replaceAll("%tps%", new DecimalFormat("##.##").format(ServerUtil.getTPS())));

        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                Config.ACCENT_ONE + "Description: &7" + check.getCheckInfo().description() +
                        "\n" + Config.ACCENT_ONE + "Info: &7" + info +
                        "\n" + Config.ACCENT_ONE + "Ping: &7" + PlayerUtil.getPing(data.getPlayer()) +
                        "\n" + Config.ACCENT_ONE + "TPS: &7" + String.format("%.2f", PacketEvents.get().getServerUtils().getTPS()) +
                        "\n" + Config.ACCENT_TWO + "Click to teleport.")).create()));

        final MedusaSendAlertEvent event = new MedusaSendAlertEvent(alertMessage, data.getPlayer(), check, info);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        alerts.forEach(data1 -> data1.getPlayer().spigot().sendMessage(alertMessage));
    }

    public enum ToggleAlertType {
        ADD, REMOVE;
    }
}
