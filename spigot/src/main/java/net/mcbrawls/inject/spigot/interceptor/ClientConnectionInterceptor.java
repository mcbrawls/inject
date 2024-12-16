package net.mcbrawls.inject.spigot.interceptor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private Method getMethodByReturnType(Class<?> clazz, Class<?> returnType) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter((method) -> method.getReturnType().equals(returnType))
                .findFirst()
                .get();
    }

    // Predicate is needed to make sure we find the `List<ChannelFuture> channels` instead of `List<Connection> connections`
    // since `channels` is private.
    private Field getFieldByTypeAndPredicate(Class<?> clazz, Class<?> type, Function<Field, Boolean> predicate) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter((field) -> field.getType().equals(type) && predicate.apply(field))
                .findFirst()
                .get();
    }

    private List<ChannelFuture> getChannels() {
        final String serverVersion = this.getServerVersion();

        try {
            final Class<?> minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");

            final Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".CraftServer");
            final Object craftServerObject = craftServerClass.cast(Bukkit.getServer());
            final Method craftServerGetServerMethod = craftServerClass.getDeclaredMethod("getServer");

            final Object minecraftServer = craftServerGetServerMethod.invoke(craftServerObject);
            final Method getConnectionMethod = getMethodByReturnType(minecraftServerClass, Class.forName("net.minecraft.server.network.ServerConnection"));

            final Object serverConnection = getConnectionMethod.invoke(minecraftServer);
            final Field channelsField = getFieldByTypeAndPredicate(serverConnection.getClass(), List.class, (field) -> Modifier.isPrivate(field.getModifiers()));

            channelsField.setAccessible(true);
            return (List<ChannelFuture>) channelsField.get(serverConnection);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getServerVersion() {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }
}
