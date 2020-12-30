package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.impl.combat.aimassist.*;
import com.gladurbad.medusa.check.impl.combat.autoclicker.AutoClickerA;
import com.gladurbad.medusa.check.impl.combat.autoclicker.AutoClickerB;
import com.gladurbad.medusa.check.impl.combat.autoclicker.AutoClickerC;
import com.gladurbad.medusa.check.impl.combat.autoclicker.AutoClickerD;
import com.gladurbad.medusa.check.impl.combat.hitbox.HitBoxA;
import com.gladurbad.medusa.check.impl.combat.hitbox.HitBoxB;
import com.gladurbad.medusa.check.impl.combat.hitbox.HitBoxC;
import com.gladurbad.medusa.check.impl.combat.killaura.*;
import com.gladurbad.medusa.check.impl.combat.velocity.VelocityA;
import com.gladurbad.medusa.check.impl.combat.velocity.VelocityB;
import com.gladurbad.medusa.check.impl.movement.fastclimb.FastClimbA;
import com.gladurbad.medusa.check.impl.movement.fly.FlyA;
import com.gladurbad.medusa.check.impl.movement.fly.FlyB;
import com.gladurbad.medusa.check.impl.movement.fly.FlyC;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusA;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusB;
import com.gladurbad.medusa.check.impl.movement.motion.MotionA;
import com.gladurbad.medusa.check.impl.movement.motion.MotionB;
import com.gladurbad.medusa.check.impl.movement.motion.MotionC;
import com.gladurbad.medusa.check.impl.movement.motion.MotionD;
import com.gladurbad.medusa.check.impl.movement.speed.SpeedA;
import com.gladurbad.medusa.check.impl.movement.speed.SpeedB;
import com.gladurbad.medusa.check.impl.movement.speed.SpeedC;
import com.gladurbad.medusa.check.impl.player.hand.HandA;
import com.gladurbad.medusa.check.impl.player.hand.HandB;
import com.gladurbad.medusa.check.impl.player.hand.HandC;
import com.gladurbad.medusa.check.impl.player.protocol.*;
import com.gladurbad.medusa.check.impl.player.scaffold.ScaffoldA;
import com.gladurbad.medusa.check.impl.player.timer.TimerA;
import com.gladurbad.medusa.check.impl.player.timer.TimerB;
import com.gladurbad.medusa.check.impl.player.timer.TimerC;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.data.PlayerData;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CheckManager {

    public static final Class[] CHECKS = new Class[] {
            AimAssistA.class,
            AimAssistB.class,
            AimAssistC.class,
            AimAssistD.class,
            AimAssistE.class,
            AimAssistF.class,
            AimAssistG.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            KillAuraA.class,
            KillAuraB.class,
            KillAuraC.class,
            KillAuraD.class,
            KillAuraE.class,
            KillauraF.class,
            HitBoxA.class,
            HitBoxB.class,
            HitBoxC.class,
            VelocityA.class,
            VelocityB.class,
            FastClimbA.class,
            FlyA.class,
            FlyB.class,
            FlyC.class,
            JesusA.class,
            JesusB.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            TimerA.class,
            TimerB.class,
            TimerC.class,
            HandA.class,
            HandB.class,
            HandC.class,
            ScaffoldA.class,
            ProtocolA.class,
            ProtocolB.class,
            ProtocolC.class,
            ProtocolD.class,
            ProtocolE.class,
            ProtocolF.class,
            ProtocolG.class,
            ProtocolH.class,
            ProtocolI.class,
            ProtocolJ.class
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }

    public static void setup() {
        for (Class clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
                    //Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
            }
        }
    }
}

