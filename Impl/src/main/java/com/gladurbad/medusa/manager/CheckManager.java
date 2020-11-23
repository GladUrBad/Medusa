package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.playerdata.PlayerData;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class CheckManager {

    @Getter
    private final List<Class<? extends Check>> checks = new ArrayList<>();

    private final List<Constructor<?>> constructors = new ArrayList<>();

    public void registerChecks() {
        try (ScanResult scan = new ClassGraph().enableAllInfo().acceptPackages(Medusa.class.getPackage().getName()).scan()) {
            checks.addAll(scan.getSubclasses(Check.class.getName()).loadClasses(Check.class));
        }

        for (final Class<? extends Check> cl : checks) {
            try {
                constructors.add(cl.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            if (!Config.ENABLED_CHECKS.contains(constructor.getDeclaringClass().getSimpleName())) continue;
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return checkList;
    }

    public void disInit() {
        this.checks.clear();
        this.constructors.clear();
    }
}