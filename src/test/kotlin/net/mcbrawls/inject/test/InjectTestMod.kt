package net.mcbrawls.inject.test

import io.netty.channel.ChannelHandlerContext
import net.fabricmc.api.DedicatedServerModInitializer
import net.mcbrawls.inject.http.HttpByteBuf
import net.mcbrawls.inject.http.HttpInjector
import net.mcbrawls.inject.http.HttpRequest
import net.mcbrawls.inject.http.httpBuffer

class InjectTestMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        object : HttpInjector() {
            override fun intercept(ctx: ChannelHandlerContext, request: HttpRequest): HttpByteBuf {
                val buffer = ctx.httpBuffer()
                buffer.writeStatusLine("1.1", 200, "OK")
                buffer.writeText("You have successfully received a response.")
                return buffer
            }
        }.register()
    }
}
