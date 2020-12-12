package com.gladurbad.medusa;

import com.gladurbad.medusa.command.CommandManager;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.listener.BukkitEventListener;
import com.gladurbad.medusa.listener.ClientBrandListener;
import com.gladurbad.medusa.listener.JoinQuitListener;
import com.gladurbad.medusa.listener.NetworkListener;
import com.gladurbad.medusa.manager.CheckManager;
import com.gladurbad.medusa.manager.PlayerDataManager;
import com.gladurbad.medusa.manager.ThemeManager;
import com.gladurbad.medusa.manager.TickManager;
import com.gladurbad.medusa.packet.processor.ReceivingPacketProcessor;
import com.gladurbad.medusa.packet.processor.SendingPacketProcessor;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.Messenger;

@Getter
public enum Medusa {

    INSTANCE;

    private MedusaPlugin plugin;

    private long startTime;

    private final CheckManager checkManager = new CheckManager();
    private final ThemeManager themeManager = new ThemeManager();

    private final TickManager tickManager = new TickManager();
    private final ReceivingPacketProcessor receivingPacketProcessor = new ReceivingPacketProcessor();
    private final SendingPacketProcessor sendingPacketProcessor = new SendingPacketProcessor();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final CommandManager commandManager = new CommandManager(this.getPlugin());

    public void start(final MedusaPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while starting Medusa.";

        this.getPlugin().saveDefaultConfig();

        Config.updateConfig();

        this.checkManager.setup();

        this.themeManager.setup();

        Bukkit.getOnlinePlayers().forEach(player -> this.getPlayerDataManager().add(player));

        getPlugin().saveDefaultConfig();
        getPlugin().getCommand("medusa").setExecutor(commandManager);

        tickManager.start();

        final Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(plugin, "MC|Brand", new ClientBrandListener());

        startTime = System.currentTimeMillis();

        Bukkit.getServer().getPluginManager().registerEvents(new BukkitEventListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ClientBrandListener(), plugin);

        PacketEvents.getSettings()
                .injectAsync(true)
                .ejectAsync(true)
                .injectEarly(true)
                .packetHandlingThreadCount(1)
                .checkForUpdates(true)
                .backupServerVersion(ServerVersion.v_1_8_8);

        PacketEvents.getAPI().getEventManager().registerListener(new NetworkListener());
        PacketEvents.getAPI().getEventManager().registerListener(new JoinQuitListener());
    }

    public void stop(final MedusaPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while shutting down Medusa.";

        tickManager.stop();
    }
}
