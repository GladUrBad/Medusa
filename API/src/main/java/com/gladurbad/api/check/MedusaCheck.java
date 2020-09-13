package com.gladurbad.api.check;

public interface MedusaCheck {

    CheckInfo getCheckInfo();

    boolean isSetback();

    boolean isEnabled();

    int getMaxVl();

    int getVl();
}
