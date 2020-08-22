package com.gladurbad.antimovehack.managers;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.impl.fastclimb.FastClimbA;
import com.gladurbad.antimovehack.check.impl.flight.FlightA;
import com.gladurbad.antimovehack.check.impl.jesus.JesusA;
import com.gladurbad.antimovehack.check.impl.motion.MotionA;
import com.gladurbad.antimovehack.check.impl.speed.SpeedA;
import com.gladurbad.antimovehack.check.impl.timer.TimerA;
import com.gladurbad.antimovehack.playerdata.PlayerData;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


public class CheckManager {

    public static final Class[] CHECKS = new Class[] {
            FastClimbA.class,
            FlightA.class,
            JesusA.class,
            MotionA.class,
            SpeedA.class,
            TimerA.class
    };

    public static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static void registerChecks() {
        for(Class clazz : CHECKS) {
            try {
                CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }
    }


    public static List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for(Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return checkList;
    }

}
