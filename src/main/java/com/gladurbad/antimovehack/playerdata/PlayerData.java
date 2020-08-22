package com.gladurbad.antimovehack.playerdata;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.UUID;


public class PlayerData {

    @Getter
    private final Player player;
    @Getter
    private final UUID playerUUID;

    public PlayerData(UUID playerUUID, Player player) {
        this.playerUUID = playerUUID;
        this.player = player;
    }

    //Cheat listener thresholds.
    public int flightThreshold;
    public int speedThreshold;
    public int fastClimbThreshold;
    public int jesusThreshold;
    public int jesusBThreshold;

    //Movement info per check, I don't care, I'm lazy.
    public double flightLastDeltaY;
    public double speedLastDeltaXZ;
    public long timerLastTime;

    //Legit location storing;
    public Location flightLastLegitLocation;
    public Location speedLastLegitLocation;
    public Location fastClimbLastLegitLocation;
    public Location jesusLastLegitLocation;
    public Location jesusBLastLegitLocation;

    //Cheat listener violation levels.
    public int flightViolationLevel;
    public int speedViolationLevel;
    public int fastClimbViolationLevel;
    public int timerViolationLevel;
    public int motionViolationLevel;
    public int jesusViolationLevel;
    public int jesusBViolationLevel;

    //Miscellanious data per check.
    public Deque<Long> timerDifferences = Lists.newLinkedList();
    public int timerTeleportTicks;


}
