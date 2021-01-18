package com.gladurbad.medusa.util.type;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RayTrace {

    private final Vector origin;
    private final Vector direction;

    public RayTrace(final Vector origin, final Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public static RayTrace from(final Player player) {
        return new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
    }

    public double origin(int i) {
        switch (i) {
            case 0:
                return origin.getX();
            case 1:
                return origin.getY();
            case 2:
                return origin.getZ();
            default:
                return 0;
        }
    }

    public double direction(int i) {
        switch (i) {
            case 0:
                return direction.getX();
            case 1:
                return direction.getY();
            case 2:
                return direction.getZ();
            default:
                return 0;
        }
    }
}
