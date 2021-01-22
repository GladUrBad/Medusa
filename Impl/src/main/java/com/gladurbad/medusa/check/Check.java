package com.gladurbad.medusa.check;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.api.check.MedusaCheck;
import com.gladurbad.api.listener.MedusaFlagEvent;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.data.processor.*;
import com.gladurbad.medusa.util.anticheat.PunishUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.util.anticheat.AlertUtil;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
@Setter
public abstract class Check implements MedusaCheck {

    //Data for check.
    public final PlayerData data;

    //Check data from config.
    private final boolean enabled;
    private final int maxVl;
    private final String punishCommand;

    //Check information.
    private int vl;
    private long lastFlagTime;
    private CheckType checkType;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    public double buffer;

    //Because I'm lazy to change the annotation again.
    public String justTheName;
    public char type;

    public Check(final PlayerData data) {
        this.data = data;

        this.enabled = Config.ENABLED_CHECKS.contains(this.getClass().getSimpleName());
        this.maxVl = Config.MAX_VIOLATIONS.get(this.getClass().getSimpleName());
        this.punishCommand = Config.PUNISH_COMMANDS.get(this.getClass().getSimpleName());

        this.checkType = CheckType.fromPackageName(this.getClass().getPackage().getName());

        this.justTheName = this.getCheckInfo().name().split("\\(")[0].replace(" ", "");
        this.type = this.getCheckInfo().name().split("\\(")[1].split("\\)")[0].replaceAll(" ", "").toCharArray()[0];
    }

    public abstract void handle(final Packet packet);

    public void fail(final Object info) {
        if (System.currentTimeMillis() - lastFlagTime > Config.ALERT_COOLDOWN && vl >= Config.MIN_VL_TO_ALERT) {
            AlertUtil.handleAlert(this, data, Objects.toString(info));
            this.lastFlagTime = System.currentTimeMillis();
        }

        if (vl > maxVl) {
            PunishUtil.punish(this, data);
        }
    }

    public void fail() {
        fail("No information.");
    }

    protected boolean isExempt(final ExemptType exemptType) {
        return data.getExemptProcessor().isExempt(exemptType);
    }

    protected boolean isExempt(final ExemptType...exemptTypes) {
        return data.getExemptProcessor().isExempt(exemptTypes);
    }

    protected long now() {
        return System.currentTimeMillis();
    }

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public void debug(final Object... objects) {
        for (final Object object : objects) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "[Medusa-Debug] " + ChatColor.GRAY + object);
        }
    }

    public enum CheckType {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player");

        private final String name;

        CheckType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static CheckType fromPackageName(String packageName) {
            for (CheckType checkType : CheckType.values()) {
                if (packageName.contains(checkType.getName().toLowerCase())) {
                    return checkType;
                }
            }
            return null;
        }
    }

    public boolean isBridging() {
        return data.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR;
    }
}