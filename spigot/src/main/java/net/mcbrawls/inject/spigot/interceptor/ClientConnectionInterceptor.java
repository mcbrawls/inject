package net.mcbrawls.inject.spigot.interceptor;

import io.netty.channel.*;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

public class ClientConnectionInterceptor {

    public void install(Consumer<Channel> channelConsumer) {
        final ChannelInitializer<?> beginInitProtocol = new ChannelInitializer<>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                try {
                    channelConsumer.accept(channel);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot inject incoming channel " + channel, e);
                }
            }

        };

        final ChannelInboundHandler serverHandler = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Channel channel = (Channel) msg;

                // Prepare to initialize ths channel
                channel.pipeline().addFirst(beginInitProtocol);
                ctx.fireChannelRead(msg);
            }

        };

        final List<ChannelFuture> channels = this.getChannels();
        for (final ChannelFuture channelFuture : channels) {
            channelFuture.channel().pipeline().addFirst(serverHandler);
        }
    }

    private List<ChannelFuture> getChannels() {
        final String serverVersion = this.getServerVersion();

        try {
            final Class<?> minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");

            final Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftServer");
            final Object craftServerObject = craftServerClass.cast(Bukkit.getServer());
            final Method craftServerGetServerMethod = craftServerClass.getDeclaredMethod("getServer");

            final Object minecraftServer = craftServerGetServerMethod.invoke(craftServerObject);
            final Method getConnectionMethod = minecraftServerClass.getDeclaredMethod("ai");

            final Object serverConnection = getConnectionMethod.invoke(minecraftServer);
            final Field channelsField = serverConnection.getClass().getDeclaredField("f");

            channelsField.setAccessible(true);
            return (List<ChannelFuture>) channelsField.get(serverConnection);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private String getServerVersion() {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }
}