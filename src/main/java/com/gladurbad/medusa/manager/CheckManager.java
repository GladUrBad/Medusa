package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.impl.combat.autoclicker.AutoclickerA;
import com.gladurbad.medusa.check.impl.combat.killaura.*;
import com.gladurbad.medusa.check.impl.combat.reach.ReachA;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusC;
import com.gladurbad.medusa.check.impl.movement.motion.*;
import com.gladurbad.medusa.check.impl.player.badpackets.BadPacketsA;
import com.gladurbad.medusa.check.impl.movement.fastclimb.FastClimbA;
import com.gladurbad.medusa.check.impl.movement.flight.FlightA;
import com.gladurbad.medusa.check.impl.movement.flight.FlightB;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusA;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusB;
import com.gladurbad.medusa.check.impl.movement.nofall.NofallA;
import com.gladurbad.medusa.check.impl.movement.speed.SpeedA;
import com.gladurbad.medusa.check.impl.movement.speed.SpeedB;
import com.gladurbad.medusa.check.impl.player.badpackets.BadPacketsB;
import com.gladurbad.medusa.check.impl.player.badpackets.BadPacketsC;
import com.gladurbad.medusa.check.impl.player.scaffold.ScaffoldA;
import com.gladurbad.medusa.check.impl.player.scaffold.ScaffoldB;
import com.gladurbad.medusa.check.impl.player.scaffold.ScaffoldC;
import com.gladurbad.medusa.check.impl.player.timer.TimerA;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.playerdata.PlayerData;
import org.bukkit.Bukkit;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


public class CheckManager {

    public static final Class[] CHECKS = new Class[] {
            AutoclickerA.class,
            KillauraA.class,
            KillauraB.class,
            KillauraC.class,
            KillauraD.class,
            KillauraE.class,
            KillauraF.class,
            KillauraG.class,
            ReachA.class,
            FastClimbA.class,
            FlightA.class,
            FlightB.class,
            JesusA.class,
            JesusB.class,
            JesusC.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            MotionE.class,
            MotionF.class,
            NofallA.class,
            SpeedA.class,
            SpeedB.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            ScaffoldA.class,
            ScaffoldB.class,
            ScaffoldC.class,
            TimerA.class
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static void registerChecks() {
        for (Class clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
            }
        }
    }

    public static List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return checkList;
    }
}