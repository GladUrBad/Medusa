package com.gladurbad.medusa.config;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.manager.CheckManager;
import com.gladurbad.medusa.util.ChatUtil;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.*;

public class Config {

    public static boolean TESTMODE;

    public static String PREFIX;
    public static String NO_PERMS;
    public static String COMMAND_PREFIX;

    public static int VL_TO_ALERT;
    public static String ALERT_FORMAT;
    public static String COMMAND_NAME;
    public static int CLEAR_VIOLATIONS_DELAY;


    public static List<String> ENABLED_CHECKS = new ArrayList<>();
    public static List<String> SETBACK_CHECKS = new ArrayList<>();
    public static Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public static Map<String, String> PUNISH_COMMANDS = new HashMap<>();


    public static void updateConfig() {
        try {
            TESTMODE = getBooleanFromConfig("testmode");

            PREFIX = ChatUtil.color(getStringFromConfig("response.general.prefix"));
            NO_PERMS = getStringFromConfig("response.general.no-permission");
            COMMAND_NAME = getStringFromConfig("response.command.name");
            CLEAR_VIOLATIONS_DELAY = getIntegerFromConfig("violations.clear-violations-delay");
            COMMAND_PREFIX = ChatUtil.color(getStringFromConfig("response.command.prefix"));

            VL_TO_ALERT = getIntegerFromConfig("violations.minimum-vl");
            ALERT_FORMAT = getStringFromConfig("violations.alert-format");

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
                                value.setValue(getBooleanFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDoubleFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + "." + name));
                                break;
                            case STRING:
                                value.setValue(getStringFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLongFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + "." + name));
                                break;
                        }
                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBooleanFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".enabled");
                final int maxViolations = getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".max-violations");
                final String punishCommand = getStringFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".punish-com.gladurbad.medusa.command");

                if (checkType.equals("movement")) {
                    final boolean setBack = getBooleanFromConfig("checks.movement." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".setback");
                    if (setBack) {
                        SETBACK_CHECKS.add(check.getSimpleName());
                    }
                }

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
        return Medusa.getInstance().getConfig().getBoolean(string);
    }

    public static String getStringFromConfig(String string) {
        return Medusa.getInstance().getConfig().getString(string);
    }

    private static int getIntegerFromConfig(String string) {
        return Medusa.getInstance().getConfig().getInt(string);
    }

    private static double getDoubleFromConfig(String string) {
        return Medusa.getInstance().getConfig().getDouble(string);
    }

    private static long getLongFromConfig(String string) {
        return Medusa.getInstance().getConfig().getLong(string);
    }
}
