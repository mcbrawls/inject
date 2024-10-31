package net.mcbrawls.inject.spigot;

import net.mcbrawls.inject.api.InjectPlatform;
import net.mcbrawls.inject.api.Injector;
import net.mcbrawls.inject.spigot.interceptor.ClientConnectionInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InjectSpigot implements InjectPlatform {
    private static final Logger LOGGER = Logger.getLogger("inject");
    public static InjectSpigot INSTANCE = new InjectSpigot();
    private static final List<Injector> injectors = new ArrayList<>();
    private static boolean hasInitialized = false;

    private InjectSpigot() {
    }

    @Override
    public void registerInjector(Injector injector) {
        injectors.add(injector);

        if (!hasInitialized) {
            new ClientConnectionInterceptor().install((channel) -> {
                var pipeline = channel.pipeline();
                injectors.forEach(pipeline::addFirst);
            });

            hasInitialized = true;
            LOGGER.info("Inject initialized");
        }
    }
}