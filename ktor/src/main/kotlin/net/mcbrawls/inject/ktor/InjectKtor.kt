package net.mcbrawls.inject.ktor

import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.jakarta.Jetty
import io.ktor.server.jetty.jakarta.JettyApplicationEngine
import io.ktor.server.jetty.jakarta.JettyApplicationEngineBase
import net.mcbrawls.inject.api.InjectPlatform
import net.mcbrawls.inject.jetty.JettyInjector
import org.eclipse.jetty.server.LocalConnector

fun jettyKtorServer(platform: InjectPlatform, module: Application.() -> Unit = {}) = embeddedServer(
    Jetty,
    configure = {
        configureServer = {
            val connector = LocalConnector(this)
            server.connectors = arrayOf(connector)

            platform.registerInjector(JettyInjector(connector))
        }
    },
    module = module,
)

fun EmbeddedServer<JettyApplicationEngine, JettyApplicationEngineBase.Configuration>.startOnNewThread() {
    Thread({
        start(wait = true)
    }, "Ktor").start()
}
