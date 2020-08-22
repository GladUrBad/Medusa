package com.gladurbad.antimovehack.listener.nettylisteners;

import com.gladurbad.antimovehack.AntiMoveHack;
import com.gladurbad.antimovehack.event.FlyingEvent;
import com.gladurbad.antimovehack.event.ServerTeleportEvent;
import com.gladurbad.antimovehack.event.ServerVelocityEvent;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class PacketListener implements Listener {

    public static ArrayList<UUID> INJECTED_PLAYERS = new ArrayList<>();

    public static void create(final Player player) {
        ChannelDuplexHandler cdh = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                super.channelRead(context, packet);
                if(AntiMoveHack.getAntiMoveHack().isEnabled()) {
                    if (packet instanceof PacketPlayInFlying) {
                        Bukkit.getScheduler().runTask(AntiMoveHack.getAntiMoveHack(), () -> Bukkit.getPluginManager().callEvent(
                                new FlyingEvent(player, false)));
                    }
                }
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                super.write(context, packet, channelPromise);
                if(AntiMoveHack.getAntiMoveHack().isEnabled()) {
                    if (packet instanceof PacketPlayOutPosition) {
                        Bukkit.getScheduler().runTask(AntiMoveHack.getAntiMoveHack(), () -> Bukkit.getPluginManager().callEvent(
                                new ServerTeleportEvent(player, false)));
                    }

                    if (packet instanceof PacketPlayOutEntityVelocity) {
                        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer(0));
                        ((PacketPlayOutEntityVelocity) packet).b(serializer);
                        int id = serializer.readInt();
                        if (id != player.getEntityId()) {
                            return;
                        }

                        double x = serializer.readShort() / 8000D;
                        double y = serializer.readShort() / 8000D;
                        double z = serializer.readShort() / 8000D;
                        Vector velocity = new Vector(x, y, z);

                        Bukkit.getScheduler().runTask(AntiMoveHack.getAntiMoveHack(), () -> Bukkit.getPluginManager().callEvent(
                                new ServerVelocityEvent(player, false, velocity)));
                    }
                }
            }
        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), cdh);
    }

    public static void removePlayer(final Player p) {
        Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
        });
    }
}


