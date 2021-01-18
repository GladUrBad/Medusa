package com.gladurbad.medusa.data.processor;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import com.gladurbad.medusa.util.type.EvictingList;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import lombok.Getter;
import com.gladurbad.medusa.data.PlayerData;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public class ActionProcessor {

    private final PlayerData data;

    private final EvictingList<Long> flyingSamples = new EvictingList<>(50);

    private boolean sprinting, sneaking, sendingAction, placing, digging, blocking,
            respawning, sendingDig, lagging;

    @Setter private boolean inventory;

    private int heldItemSlot, lastHeldItemSlot;

    private int lastDiggingTick, lastPlaceTick, lastBreakTick;

    private int sprintingTicks, sneakingTicks;

    private long lastFlyingTime;

    private long ping;

    public ActionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleEntityAction(final WrappedPacketInEntityAction wrapper) {
        sendingAction = true;
        switch (wrapper.getAction()) {
            case START_SPRINTING:
                sprinting = true;
                break;
            case STOP_SPRINTING:
                sprinting = false;
                break;
            case START_SNEAKING:
                sneaking = true;
                break;
            case STOP_SNEAKING:
                sneaking = false;
                break;
        }
    }

    public void handleBlockDig(final WrappedPacketInBlockDig wrapper) {
        sendingDig = true;
        switch (wrapper.getDigType()) {
            case START_DESTROY_BLOCK:
                digging = true;
                break;
            case STOP_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
                digging = false;
                break;
            case RELEASE_USE_ITEM:
                blocking = true;
                break;
        }
    }

    public void handleClientCommand(final WrappedPacketInClientCommand wrapper) {
        switch (wrapper.getClientCommand()) {
            case OPEN_INVENTORY_ACHIEVEMENT:
                inventory = true;
                break;
            case PERFORM_RESPAWN:
                respawning = true;
                break;
        }
    }

    public void handleHeldItemSlot(final WrappedPacketInHeldItemSlot wrapper) {
        this.lastHeldItemSlot = this.heldItemSlot;
        this.heldItemSlot = wrapper.getCurrentSelectedSlot();
    }

    public void handleBlockPlace() {
        placing = true;
    }

    public void handleCloseWindow() {
        inventory = false;
    }

    public void handleArmAnimation() {
        /*
         This can be disabled if the client sends a dig packet then immediately start clicking
         Which makes it so the player is immune to AutoClicker checks due to his Digging state.
         Getting the looking block ensures that the player is not spoofing his digging state.
         */
        if (digging && PlayerUtil.getLookingBlock(data.getPlayer(), 5) != null) {
            lastDiggingTick = Medusa.INSTANCE.getTickManager().getTicks();
        }
    }

    public void handleInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            lastDiggingTick = Medusa.INSTANCE.getTickManager().getTicks();
        }
    }

    public void handleBukkitPlace() {
        lastPlaceTick = Medusa.INSTANCE.getTickManager().getTicks();
    }

    public void handleBukkitBlockBreak() {
        lastBreakTick = Medusa.INSTANCE.getTickManager().getTicks();
    }

    public void handleFlying() {
        blocking = false;
        sendingDig = false;
        sendingAction = false;
        placing = false;
        respawning = false;

        sprintingTicks = sprinting ? sprintingTicks + 1 : 0;
        sneakingTicks = sneaking ? sneakingTicks + 1 : 0;

        final long delay = System.currentTimeMillis() - lastFlyingTime;

        if (delay > 0) {
            flyingSamples.add(delay);
        }

        if (flyingSamples.isFull()) {
            final double deviation = MathUtil.getStandardDeviation(flyingSamples);
            lagging = deviation > 120;
        }
        lastFlyingTime = System.currentTimeMillis();
        ping = PacketEvents.get().getPlayerUtils().getPing(data.getPlayer());
    }
}