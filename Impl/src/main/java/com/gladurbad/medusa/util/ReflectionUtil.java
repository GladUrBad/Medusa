package com.gladurbad.medusa.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import com.gladurbad.medusa.util.type.BoundingBox;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public final class ReflectionUtil {

    /**
     * We can make a caching system for this in order to be somewhat more efficient.
     * Reflection tends to be heavy, especially if we're using it every tick.
     * Maps would be great!
     */

    private String versionString;

    public Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        try {
            return clazz.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersion() {
        if (versionString == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }

        return versionString;
    }

    public Class<?> getNMSClass(String nmsClassName) {
        final String clazzName = "net.minecraft.server." + getVersion() + nmsClassName;
        try {
            return Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public Class<?> getOBCClass(String obcClassName) {
        final String clazzName = "org.bukkit.craftbukkit." + getVersion() + obcClassName;
        try {
            return Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static Object craftEntity(Entity entity) {
        try {
            return getMethod(getOBCClass("entity.CraftEntity"), "getHandle").invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BoundingBox getBoundingBox(Entity entity) {
        try {
            final Object nmsBoundingBox = getMethod(getNMSClass("Entity"), "getBoundingBox")
                    .invoke(craftEntity(entity));

            return toBoundingBox(nmsBoundingBox);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BoundingBox toBoundingBox(Object aaBB) {

        final Vector min = getBoxMin(aaBB);
        final Vector max = getBoxMax(aaBB);

        return new BoundingBox(min, max);
    }

    private Vector getBoxMin(Object box) {

        double x = 0D;
        double y = 0D;
        double z = 0D;

        Class<?> boxClass = box.getClass();

        try {
            if (!ServerUtil.isHigherThan1_13_2()) {
                x = (double) getField(boxClass, "a").get(box);
                y = (double) getField(boxClass, "b").get(box);
                z = (double) getField(boxClass, "c").get(box);
            } else {
                x = (double) getField(boxClass, "minX").get(box);
                y = (double) getField(boxClass, "minY").get(box);
                z = (double) getField(boxClass, "minZ").get(box);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new Vector(x, y, z);
    }

    private Vector getBoxMax(Object box) {

        double x = 0D;
        double y = 0D;
        double z = 0D;

        Class<?> boxClass = box.getClass();

        try {
            if (!ServerUtil.isHigherThan1_13_2()) {
                x = (double) getField(boxClass, "d").get(box);
                y = (double) getField(boxClass, "e").get(box);
                z = (double) getField(boxClass, "f").get(box);
            } else {
                x = (double) getField(boxClass, "maxX").get(box);
                y = (double) getField(boxClass, "maxY").get(box);
                z = (double) getField(boxClass, "maxZ").get(box);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new Vector(x, y, z);
    }
}