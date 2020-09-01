package com.gladurbad.medusa.config;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.manager.CheckManager;
import com.gladurbad.medusa.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.*;

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

    //public static int MAX_CPS;
    //public static double MAX_REACH;
    //public static int REACH_SENSITIVITY;
    //public static long REACH_MAXLATENCY;

    public static List<String> ENABLED_CHECKS = new ArrayList<>();
    public static List<String> SETBACK_CHECKS = new ArrayList<>();
    public static Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public static Map<String, Double> VL_ADD = new HashMap<>();
    public static Map<String, Integer> VL_DECAY = new HashMap<>();
    public static Map<String, Integer> VL_DELAY = new HashMap<>();
    public static Map<String, List<List<String>>> PUNISH_COMMANDS = new HashMap<>();
    private static Map<String, String> TYPES = new HashMap<>();

    public static String getType(String s) {
        return TYPES.get(s);
    }

    public static void updateConfig() {
        try {
            TESTMODE = getBooleanFromConfig("testmode");

            PREFIX = ChatUtil.color(getStringFromConfig("response.general.prefix"));
            NO_PERMS = getStringFromConfig("response.general.no-permission");
            COMMAND_NAME = getStringFromConfig("response.command.name");
            COMMAND_PREFIX = ChatUtil.color(getStringFromConfig("response.command.prefix"));

            VL_TO_ALERT = getIntegerFromConfig("response.violations.minimum-vl");
            ALERT_FORMAT = getStringFromConfig("response.violations.alert-format");

            //MAX_CPS = getIntegerFromConfig("checks.combat.max-cps");
            //MAX_REACH = getDoubleFromConfig("checks.combat.max-reach");
            //REACH_SENSITIVITY = getIntegerFromConfig("checks.combat.reach-sensitivity");
            //REACH_MAXLATENCY = (long) getLongFromConfig("checks.combat.reach-maxlatency");
            FileConfiguration config = Medusa.getInstance().getConfig();
            for (String s : config.getConfigurationSection("checks").getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection("checks." + s);
                List<List<String>> commands = new ArrayList<>();
                PUNISH_COMMANDS.put(s, commands);
                for (String part : section.getConfigurationSection("commands").getKeys(false)) {
                    Object o = section.get("commands." + part);
                    int i = Integer.parseInt(part);
                    if (o instanceof List)
                        commands.add((List<String>) o);
                    else
                        commands.add(Collections.singletonList((String) o));
                }
            }

            for (Class<? extends Check> check : CheckManager.CHECKS) {
                final CheckInfo checkInfo = check.getAnnotation(CheckInfo.class);
                String checkType = "";
                if (check.getName().contains("combat")) {
                    checkType = "combat";
                } else if (check.getName().contains("movement")) {
                    checkType = "movement";
                } else if (check.getName().contains("player")) {
                    checkType = "player";
                }
                TYPES.put(checkInfo.name(), checkType);

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
                final int maxViolations = getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".max-vl");
                final double vlAdd = getDoubleFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".vl-add");
                final int vlDecay = getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".vl-decay");
                final int vlDelay = getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".vl-delay");
                //final String punishCommand = getStringFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".punish-command");

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
                VL_ADD.put(check.getSimpleName(), vlAdd);
                VL_DECAY.put(check.getSimpleName(), vlDecay);
                VL_DELAY.put(check.getSimpleName(), vlDelay);
                //PUNISH_COMMANDS.put(check.getSimpleName(), punishCommand);
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
