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
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
@Setter
public abstract class Check implements MedusaCheck {

    //Data for check.
    protected final PlayerData data;

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

    public Check(final PlayerData data) {
        this.data = data;

        this.enabled = Config.ENABLED_CHECKS.contains(this.getClass().getSimpleName());
        this.maxVl = Config.MAX_VIOLATIONS.get(this.getClass().getSimpleName());
        this.punishCommand = Config.PUNISH_COMMANDS.get(this.getClass().getSimpleName());

        this.checkType = CheckType.fromPackageName(this.getClass().getPackage().getName());
    }

    public abstract void handle(final Packet packet);

    public void fail(final Object info) {
        final MedusaFlagEvent event = new MedusaFlagEvent(player(), this);
        Bukkit.getScheduler().runTaskAsynchronously(Medusa.INSTANCE.getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));

        if (!event.isCancelled()) {
            ++vl;
            data.setTotalViolations(data.getTotalViolations() + 1);

            switch (checkType) {
                case COMBAT:
                    data.setCombatViolations(data.getCombatViolations() + 1);
                    break;
                case MOVEMENT:
                    data.setMovementViolations(data.getMovementViolations() + 1);
                    break;
                case PLAYER:
                    data.setPlayerViolations(data.getPlayerViolations() + 1);
                    break;
            }

            if (System.currentTimeMillis() - lastFlagTime > Config.ALERT_COOLDOWN && vl >= Config.MIN_VL_TO_ALERT) {
                AlertUtil.handleAlert(this, data, Objects.toString(info));
                this.lastFlagTime = System.currentTimeMillis();
            }

            if (vl > maxVl) {
                PunishUtil.punish(this, data);
            }
        }
    }

    public void fail() {
        fail("No information.");
    }

    protected ActionProcessor actionInfo() {
        return data.getActionProcessor();
    }

    protected ClickProcessor clickInfo() {
        return data.getClickProcessor();
    }

    protected CombatProcessor combatInfo() {
        return data.getCombatProcessor();
    }

    protected PositionProcessor positionInfo() {
        return data.getPositionProcessor();
    }

    protected RotationProcessor rotationInfo() {
        return data.getRotationProcessor();
    }

    protected VelocityProcessor velocityInfo() {
        return data.getVelocityProcessor();
    }

    protected Player player() {
        return data.getPlayer();
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
        for (Object object : objects) {
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
        return player().getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR;
    }
}