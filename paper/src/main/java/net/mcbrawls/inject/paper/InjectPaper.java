package net.mcbrawls.inject.paper;

import io.papermc.paper.network.ChannelInitializeListenerHolder;
import net.kyori.adventure.key.Key;
import net.mcbrawls.inject.api.InjectPlatform;
import net.mcbrawls.inject.api.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InjectPaper extends JavaPlugin implements InjectPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger("inject");
    public static InjectPaper INSTANCE = new InjectPaper();
    private static final List<Injector> injectors = new ArrayList<>();
    private static boolean hasInitialised = false;

    private InjectPaper() {
    }

    @Override
    public void registerInjector(Injector injector) {
        injectors.add(injector);

        if (!hasInitialised) {
            ChannelInitializeListenerHolder.addListener(Key.key("inject", "injector"), (channel) -> {
                var pipeline = channel.pipeline();
                injectors.forEach(pipeline::addFirst);
            });

            hasInitialised = true;
            LOGGER.info("Inject initialised");
        }
    }
}
