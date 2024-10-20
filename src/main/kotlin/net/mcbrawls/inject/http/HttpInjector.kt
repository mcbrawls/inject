package net.mcbrawls.inject.http

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import net.mcbrawls.inject.Injector
import net.mcbrawls.inject.InjectorContext

/**
 * An [Injector] for HTTP requests.
 */
@Sharable
abstract class HttpInjector : Injector() {
    abstract fun intercept(ctx: ChannelHandlerContext, request: HttpRequest): HttpByteBuf

    override fun isRelevant(ctx: InjectorContext): Boolean {
        return ctx.message.isRequestGet()
    }

    final override fun onRead(ctx: ChannelHandlerContext, buf: ByteBuf): Boolean {
        val request = HttpRequest.parse(buf)
        val response = intercept(ctx, request)
        ctx.writeAndFlush(response.inner).addListener(ChannelFutureListener.CLOSE)
        return true
    }

    private companion object {
        /**
         * Checks the first buffer bytes according to the method string length.
         * @return whether the buffer matches the method
         */
        fun ByteBuf.isRequestMethod(method: String): Boolean {
            method.forEachIndexed { index, char ->
                val byte = getUnsignedByte(index).toInt()
                if (byte.toChar() != char) {
                    return false
                }
            }

            return true
        }

        fun ByteBuf.isRequestGet(): Boolean {
            return isRequestMethod("GET ")
        }
    }
}
