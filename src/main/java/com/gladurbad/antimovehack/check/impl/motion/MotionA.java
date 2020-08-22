package com.gladurbad.antimovehack.check.impl.motion;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.AlertUtils;
import com.gladurbad.antimovehack.util.CollisionUtils;
import com.gladurbad.antimovehack.util.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class MotionA extends Check {

    public MotionA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        checkA(event, player, data);
        checkB(event, player, data);
        checkC(event, player, data);
    }

    public void checkA(PlayerMoveEvent event, Player player, PlayerData playerData) {
        final float deltaY = (float) (event.getTo().getY() - event.getFrom().getY());

        float expectedJumpMotion = 0.6F;

        final boolean jumped = CollisionUtils.isOnGround(event.getFrom(), -0.00001) && !CollisionUtils.isOnGround(event.getTo(), -0.00001) && deltaY > 0;

        if(PlayerUtils.getPotionLevel(player, PotionEffectType.JUMP) > 0) {
            expectedJumpMotion += PlayerUtils.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
        }

        if(deltaY > expectedJumpMotion && jumped) {
            AlertUtils.handleViolation(playerData, "Motion (A)" , playerData.motionViolationLevel++, event.getFrom());
        }
    }

    public void checkB(PlayerMoveEvent event, Player player, PlayerData playerData) {
        final float deltaY = (float) (event.getTo().getY() - event.getFrom().getY());

        float expectedJumpMotion = 0.42F;

        final boolean jumped = CollisionUtils.isOnGround(event.getFrom(), -0.00001) && !CollisionUtils.isOnGround(event.getTo(), -0.00001) && deltaY > 0;

        final boolean valid = jumped && !CollisionUtils.isCollidingWithClimbable(player) && event.getFrom().getY() % 1 == 0 && !CollisionUtils.blockNearHead(event.getTo());

        if(PlayerUtils.getPotionLevel(player, PotionEffectType.JUMP) > 0) {
            expectedJumpMotion += PlayerUtils.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
        }

        if(deltaY < expectedJumpMotion && valid) {
            AlertUtils.handleViolation(playerData, "Motion (B)" , playerData.motionBViolationLevel++, event.getFrom());
        }
    }

    public void checkC(PlayerMoveEvent event, Player player, PlayerData playerData) {
        final float deltaY = (float) (event.getTo().getY() - event.getFrom().getY());

        final boolean invalid = !CollisionUtils.isOnGround(event.getFrom(), -0.50001) &&
                !CollisionUtils.isOnGround(event.getTo(), -0.50001) &&
                deltaY > 0.0D &&
                playerData.motionCLastDeltaY <= 0.0D;

        if(invalid) {
            AlertUtils.handleViolation(playerData, "Motion (C)", playerData.motionCViolationLevel++, event.getFrom());
        }

        playerData.motionCLastDeltaY = deltaY;
    }
}
