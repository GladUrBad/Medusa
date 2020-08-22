package com.gladurbad.antimovehack.check.impl.motion;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Motion", type = "C", dev = true)
public class MotionC extends Check {

    public MotionC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PlayerMoveEvent event) {
        final float deltaY = (float) (event.getTo().getY() - event.getFrom().getY());

        final boolean invalid = !CollisionUtil.isOnGround(event.getFrom(), -0.50001) &&
                !CollisionUtil.isOnGround(event.getTo(), -0.50001) &&
                deltaY > 0.0D &&
                data.lastDeltaY <= 0.0D;

        if(invalid) {
            failAndSetback();
        } else {
            setLastLegitLocation(data.getPlayer().getLocation());
        }
    }
}
