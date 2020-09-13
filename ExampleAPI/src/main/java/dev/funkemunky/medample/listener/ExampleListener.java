package dev.funkemunky.medample.listener;

import com.gladurbad.api.listener.MedusaAlertEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExampleListener implements Listener {

    @EventHandler
    public void onEvent(MedusaAlertEvent event) {
        Bukkit.broadcastMessage("I work plz die");
    }

}
