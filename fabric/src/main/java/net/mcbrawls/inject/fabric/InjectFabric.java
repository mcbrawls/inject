package net.mcbrawls.inject.fabric;

import net.fabricmc.api.ModInitializer;
import net.mcbrawls.inject.api.InjectPlatform;
import net.mcbrawls.inject.api.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InjectFabric implements ModInitializer, InjectPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger("inject");
    public static InjectFabric INSTANCE;
    public final List<Injector> injectors = new ArrayList<>();

    @Override
    public void onInitialize() {
        INSTANCE = this;

        LOGGER.info("Inject initialising");
    }

    @Override
    public void registerInjector(Injector injector) {
        injectors.add(injector);
    }
}
