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

    private boolean isRunningPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void registerInjector(Injector injector) {
        injectors.add(injector);

        if (!hasInitialized) {
            if (isRunningPaper()) {
                LOGGER.warning("It looks like you are running Paper - Inject Spigot is unsupported on Paper!");
                LOGGER.warning("Please use Inject Paper instead!");
            }

            new ClientConnectionInterceptor().install((channel) -> {
                var pipeline = channel.pipeline();
                injectors.forEach(pipeline::addFirst);
            });

            hasInitialized = true;
            LOGGER.info("Inject initialized");
        }
    }
}