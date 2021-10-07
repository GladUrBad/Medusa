package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.util.type.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public final class TickManager implements Runnable {

    @Getter
    private int ticks;
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickProcessor has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(Medusa.INSTANCE.getPlugin(), this, 0L, 1L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        ticks++;

        Medusa.INSTANCE.getPlayerDataManager().getAllData().parallelStream()
                .forEach(data -> {
                    final Entity target = data.getCombatProcessor().getTarget();
                    final Entity lastTarget = data.getCombatProcessor().getLastTarget();
                    if(target != null && lastTarget != null) {
                        if (target != lastTarget) {
                            data.getTargetLocations().clear();
                        }
                        Location location = target.getLocation();
                        data.getTargetLocations().add(new Pair<>(location, ticks));
                    }
                });
    }

}
