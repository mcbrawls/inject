package net.mcbrawls.inject.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.mcbrawls.inject.api.InjectPlatform;
import net.mcbrawls.inject.api.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InjectVelocity extends ChannelInitializer<Channel> implements InjectPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger("inject");
    public static InjectVelocity INSTANCE = new InjectVelocity();
    private final List<Injector> injectors = new ArrayList<>();
    private boolean hasInitialized = false;
    private static Method INIT_CHANNEL;
    private ChannelInitializer<Channel> wrappedInitializer = null;

    private InjectVelocity() {
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        if (INIT_CHANNEL == null) {
            INIT_CHANNEL = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            INIT_CHANNEL.setAccessible(true);
        }
        INIT_CHANNEL.invoke(wrappedInitializer, channel);

        injectors.forEach(channel.pipeline()::addFirst);
    }

    public void init(ProxyServer server) {
        Object connectionManager = Reflection.getField(server, server.getClass(), 0, Reflection.CONNECTION_MANAGER);
        Object proxyInitializerHolder = Reflection.getField(connectionManager, Reflection.CONNECTION_MANAGER, 0, Reflection.SERVER_INITIALIZER_HOLDER);
        //noinspection unchecked
        this.wrappedInitializer = ((Supplier<ChannelInitializer<Channel>>) proxyInitializerHolder).get();
        try {
            Reflection.SET_SERVER_INITIALIZER.invoke(proxyInitializerHolder, this);
        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        hasInitialized = true;

        LOGGER.info("Inject initialized");
    }

    @Override
    public void registerInjector(Injector injector) {
        injectors.add(injector);

        if (!hasInitialized) {
            throw new RuntimeException("inject velocity needs to be initialized before registering an injector! call #init(ProxyServer)!");
        }
    }
}
