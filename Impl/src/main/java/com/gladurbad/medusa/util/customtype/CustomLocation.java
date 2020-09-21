package com.gladurbad.medusa.util.customtype;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@Getter
@Setter
public class CustomLocation {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private long timeStamp;

    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.timeStamp = System.nanoTime() / 10000;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public CustomLocation offset(double x, double y, double z, float yaw, float pitch) {
        return new CustomLocation(this.x + x, this.y + y, this.z + z, this.yaw + yaw, this.pitch + pitch, this.onGround);
    }

    public CustomLocation clone() {
        return new CustomLocation(x, y, z, yaw, pitch, onGround);
    }

    public static CustomLocation fromBukkit(Location location) {
        return new CustomLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), false);
    }

    public Location toBukkit(World world){ return new Location(world, getX(), getY(), getZ()); }

    public Vector getDirection() {
        Vector vector = new Vector();
        double rotX = this.getYaw();
        double rotY = this.getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }
}
