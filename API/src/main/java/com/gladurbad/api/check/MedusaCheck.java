package com.gladurbad.api.check;

public interface MedusaCheck {

    CheckInfo getCheckInfo();

    // Stuff for the other files
    boolean isSetback();
    boolean isEnabled();
    int getMaxVl();
    int getVl();
}
