package com.gladurbad.antimovehack.check;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class Check implements Listener {

    protected final PlayerData data;
    private int vl;
    protected double preVL;

    public Check(PlayerData data) {
        this.data = data;
        Bukkit.getServer().getPluginManager().registerEvents(this, AntiMoveHack.getAntiMoveHack());
    }

    public CheckInfo getCheckInfo() {
        return this.getClass().getAnnotation(CheckInfo.class);
    }

}
