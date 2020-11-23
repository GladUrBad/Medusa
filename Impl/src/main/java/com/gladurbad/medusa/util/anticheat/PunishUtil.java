package com.gladurbad.medusa.util.anticheat;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class PunishUtil {

    public void punish(final Check check, final PlayerData data) {
        if (!check.getPunishCommand().isEmpty()) {
            Bukkit.getScheduler().runTask(Medusa.INSTANCE.getPlugin(), () ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), check.getPunishCommand()
                            .replace("%player%", data.getPlayer().getName())
                            .replace("%check%", check.getCheckInfo().name())));
        }
    }
}
