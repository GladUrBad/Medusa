package com.gladurbad.medusa.check;

import com.gladurbad.medusa.config.Config;
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

import java.util.Objects;

@Getter
public abstract class Check {

    protected final PlayerData data;

    private int vl;
    private long lastFlagTime;
    private CheckType checkType;
    @Setter private int maxVl;
    private double buffer;
    @Setter private String punishCommand;

    public Check(final PlayerData data) {
        this.data = data;
        this.maxVl = Config.MAX_VIOLATIONS.get(this.getClass().getSimpleName());
        this.punishCommand = Config.PUNISH_COMMANDS.get(this.getClass().getSimpleName());

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

        if (System.currentTimeMillis() - lastFlagTime > Config.ALERT_COOLDOWN && vl > Config.MIN_VL_TO_ALERT) {
            AlertUtil.handleAlert(this, data, Objects.toString(info));
            this.lastFlagTime = System.currentTimeMillis();
        }
    }

    public void fail() {
        ++vl;
        fail("");
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

    public int ticks() { return Medusa.INSTANCE.getTickManager().getTicks(); }

    public double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public double increaseBufferBy(final double amount) {
        return buffer = Math.min(10000, buffer + amount);
    }

    public double decreaseBuffer() {
        return buffer = Math.max(0, buffer - 1);
    }

    public double decreaseBufferBy(final double amount) {
        return buffer = Math.max(0, buffer - amount);
    }

    public void resetBuffer() {
        buffer = 0;
    }

    public void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }

    public int hitTicks() {
        return data.getCombatProcessor().getHitTicks();
    }

    public boolean digging() {
        return data.getActionProcessor().isDigging();
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
