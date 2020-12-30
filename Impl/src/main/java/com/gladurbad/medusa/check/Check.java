package com.gladurbad.medusa.check;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.api.check.MedusaCheck;
import com.gladurbad.api.listener.MedusaFlagEvent;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.util.anticheat.PunishUtil;
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
import java.util.Objects;

@Getter
@Setter
public abstract class Check implements MedusaCheck {

    //Data for check.
    protected final PlayerData data;

    //Check data from config.
    private final boolean enabled;
    private final int maxVl;
    private final boolean setback;
    private final String punishCommand;

    //Check information.
    private int vl;
    private long lastFlagTime;
    private CheckType checkType;
    private double buffer;

    public Check(final PlayerData data) {
        this.data = data;
        this.maxVl = Config.MAX_VIOLATIONS.get(this.getClass().getSimpleName());
        this.punishCommand = Config.PUNISH_COMMANDS.get(this.getClass().getSimpleName());
        this.setback = Config.SETBACK_CHECKS.contains(this.getClass().getSimpleName());
        this.enabled = Config.ENABLED_CHECKS.contains(this.getClass().getSimpleName());

        final String packageName = this.getClass().getPackage().getName();

        if (packageName.contains("combat")) {
            checkType = CheckType.COMBAT;
        } else if (packageName.contains("movement")) {
            checkType = CheckType.MOVEMENT;
        } else if (packageName.contains("player")) {
            checkType = CheckType.PLAYER;
        }
    }

    public abstract void handle(final Packet packet);

    public void fail(final Object info) {
        final MedusaFlagEvent event = new MedusaFlagEvent(data.getPlayer(), this);
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
        fail("No info provided.");
    }

    protected boolean isExempt(final ExemptType exemptType) {
        return data.getExemptProcessor().isExempt(exemptType);
    }

    protected boolean isExempt(final ExemptType... exemptTypes) {
        return data.getExemptProcessor().isExempt(exemptTypes);
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public double increaseBuffer(final double amount) {
        return  buffer += amount;
    }

    public double decreaseBuffer() {
        return buffer = Math.max(0, buffer - 1);
    }

    public double decreaseBuffer(final double amount) {
        return buffer = Math.max(0, buffer - amount);
    }

    public void resetBuffer() {
        buffer = 0;
    }

    public void setBuffer(final double buffer) {
        this.buffer = buffer;
    }
    public void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public void debug(final Object object) {
        Bukkit.broadcastMessage(ChatColor.GREEN + "[Medusa-Debug] " + ChatColor.GRAY + object);
    }

    public void debug(final Object... objects) {
        for (Object object : objects) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "[Medusa-Debug] " + ChatColor.GRAY + object);
        }
    }

    enum CheckType {
        COMBAT, MOVEMENT, PLAYER;
    }

    public boolean isBridging() {
        return data.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR;
    }
}
