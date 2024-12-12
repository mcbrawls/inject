package net.mcbrawls.inject.examples.ktor

import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.jetty.jakarta.JettyApplicationEngine
import io.ktor.server.jetty.jakarta.JettyApplicationEngineBase
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import net.mcbrawls.inject.ktor.jettyKtorServer
import net.mcbrawls.inject.ktor.startOnNewThread
import net.mcbrawls.inject.spigot.InjectSpigot
import org.bukkit.plugin.java.JavaPlugin

class InjectKtorExample : JavaPlugin() {
    private var ktor: EmbeddedServer<JettyApplicationEngine, JettyApplicationEngineBase.Configuration>? = null

    private fun initKtor() {
        ktor = jettyKtorServer(InjectSpigot.INSTANCE) {
            routing {
                get("/") {
                    call.respondText("Hello, from Ktor!")
                }
            }
        }

        ktor!!.startOnNewThread()
    }

    override fun onEnable() {
        initKtor()
    }

    override fun onDisable() {
        ktor?.stop()
    }
}
