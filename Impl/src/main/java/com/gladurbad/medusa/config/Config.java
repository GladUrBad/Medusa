package com.gladurbad.medusa.config;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.manager.CheckManager;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.*;

public final class Config {

    //Make this an enum, please just rewrite it.
    //Appearance stuff
    public static Set<String> THEMES = new HashSet<>();
    public static String ACCENT_ONE;
    public static String ACCENT_TWO;

    //Violations stuff
    public static String ALERT_FORMAT;
    public static long ALERT_COOLDOWN;
    public static long SETBACK_COOLDOWN;
    public static int MIN_VL_TO_ALERT;
    public static int CLEAR_VIOLATIONS_DELAY;

    //Checks stuff
    public static List<String> ENABLED_CHECKS = new ArrayList<>();
    public static Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public static Map<String, String> PUNISH_COMMANDS = new HashMap<>();


    public static void updateConfig() {
        try {
            //Appearance
            THEMES = getKeysFromConfig("appearance.themes");
            ACCENT_ONE = getStringFromConfig("appearance.accents.accentOne");
            ACCENT_TWO = getStringFromConfig("appearance.accents.accentTwo");
            SETBACK_COOLDOWN = getLongFromConfig("checks.movement.setback-cooldown");

            //Violations
            ALERT_FORMAT = getStringFromConfig("violations.alert-format");
            ALERT_COOLDOWN = getLongFromConfig("violations.alert-cooldown");
            MIN_VL_TO_ALERT = getIntegerFromConfig("violations.minimum-vl-to-alert");
            CLEAR_VIOLATIONS_DELAY = getIntegerFromConfig("violations.clear-violations-delay");

            //Checks
            for (Class check : CheckManager.CHECKS) {
                final CheckInfo checkInfo = (CheckInfo) check.getAnnotation(CheckInfo.class);
                String checkType = "";
                if (check.getName().contains("combat")) {
                    checkType = "combat";
                } else if (check.getName().contains("movement")) {
                    checkType = "movement";
                } else if (check.getName().contains("player")) {
                    checkType = "player";
                }

                for (Field field : check.getDeclaredFields()) {
                    if (field.getType().equals(ConfigValue.class)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        String name = ((ConfigValue) field.get(null)).getName();
                        ConfigValue value = ((ConfigValue) field.get(null));
                        ConfigValue.ValueType type = value.getType();

                        switch (type) {
                            case BOOLEAN:
                                value.setValue(getBooleanFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getIntegerFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDoubleFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case STRING:
                                value.setValue(getStringFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLongFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                        }
                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBooleanFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + ".enabled");
                final int maxViolations = getIntegerFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + ".max-violations");
                final String punishCommand = getStringFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + ".punish-command");

                if (enabled) {
                    ENABLED_CHECKS.add(check.getSimpleName());
                }

                MAX_VIOLATIONS.put(check.getSimpleName(), maxViolations);
                PUNISH_COMMANDS.put(check.getSimpleName(), punishCommand);
            }
        } catch (Exception exception) {
            Bukkit.getLogger().severe("Could not properly load config.");
            exception.printStackTrace();
        }

    }

    private static boolean getBooleanFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getBoolean(string);
    }

    public static String getStringFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getString(string);
    }

    private static int getIntegerFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getInt(string);
    }

    public static double getDoubleFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getDouble(string);
    }

    private static List<String> getStringListFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getStringList(string);
    }

    private static Set<String> getKeysFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getConfigurationSection(string).getKeys(false);
    }

    private static long getLongFromConfig(String string) {
        return Medusa.INSTANCE.getPlugin().getConfig().getLong(string);
    }

    public static String getPathFromCheckName(String name) {
        return name.toLowerCase().replaceFirst("[(]", ".").replaceAll("[ ()]", "");
    }
}