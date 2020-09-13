package dev.funkemunky.medample;

import dev.funkemunky.medample.listener.ExampleListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ExampleListener(), this);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
