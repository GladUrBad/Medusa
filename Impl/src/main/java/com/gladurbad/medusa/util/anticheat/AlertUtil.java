package com.gladurbad.medusa.util.anticheat;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.util.ColorUtil;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import com.gladurbad.medusa.config.Config;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.Set;

@Getter
@UtilityClass
public class AlertUtil {

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
        TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%check%", check.getCheckInfo().name())
                .replaceAll("%dev%", check.getCheckInfo().experimental() ? ColorUtil.translate("&7*") : "")
                .replaceAll("%vl%", Integer.toString(check.getVl())));

        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                "&aDescription: &f" + check.getCheckInfo().description() +
                "\n&aInfo: &7" + info +
                "\n&aPing: &7" + PacketEvents.getAPI().getPlayerUtils().getPing(data.getPlayer()) +
                "\n&aTPS: &7" + String.format("%.2f", PacketEvents.getAPI().getServerUtils().getTPS()) +
                "\n&aClick to teleport.")).create()));

        alerts.forEach(player -> player.getPlayer().spigot().sendMessage(alertMessage));
    }

    public enum ToggleAlertType {
        ADD, REMOVE;
    }
}
