package net.mcbrawls.inject

import net.fabricmc.api.DedicatedServerModInitializer
import org.slf4j.LoggerFactory

object InjectMod : DedicatedServerModInitializer {
    internal val injectors = mutableListOf<Injector>()
    val LOGGER = LoggerFactory.getLogger("Inject")

    override fun onInitializeServer() {
        LOGGER.info("Inject initialising")
    }
}
