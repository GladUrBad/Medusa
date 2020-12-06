package com.gladurbad.api.check;

public interface MedusaCheck {

    CheckInfo getCheckInfo();

    String getPunishCommand();

    boolean isSetback();

    boolean isEnabled();

    int getMaxVl();

    int getVl();

    double getBuffer();

    long getLastFlagTime();
}