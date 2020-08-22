package com.gladurbad.antimovehack.check.impl.motion;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;
import com.gladurbad.antimovehack.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "B", dev = true)
public class MotionB extends Check {

    public MotionB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PlayerMoveEvent event) {
        final Player player = data.getPlayer();
        float expectedJumpMotion = 0.42F;

        final boolean jumped = CollisionUtil.isOnGround(event.getFrom(), -0.00001) && !CollisionUtil.isOnGround(event.getTo(), -0.00001) && data.deltaY > 0;

        final boolean valid = jumped && !CollisionUtil.isCollidingWithClimbable(player) && event.getFrom().getY() % 1 == 0 && !CollisionUtil.blockNearHead(event.getTo());

        if(PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) > 0) {
            expectedJumpMotion += PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
        }

        if(data.deltaY < expectedJumpMotion && valid) {
            failAndSetback();
        } else {
            setLastLegitLocation(data.getPlayer().getLocation());
        }
    }
}
