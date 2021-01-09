package com.gladurbad.api.check;

public interface MedusaCheck {

    CheckInfo getCheckInfo();

    String getPunishCommand();

    boolean isEnabled();

    int getMaxVl();

    int getVl();

    long getLastFlagTime();
}