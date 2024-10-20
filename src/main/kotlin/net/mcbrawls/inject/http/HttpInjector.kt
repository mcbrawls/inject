package net.mcbrawls.inject.http

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import net.mcbrawls.inject.Injector
import net.mcbrawls.inject.InjectorContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * An [Injector] for HTTP requests.
 */
@Sharable
abstract class HttpInjector : Injector() {
    private val logger: Logger = LoggerFactory.getLogger("HttpInjector ${hashCode()}")

    abstract fun intercept(ctx: ChannelHandlerContext, request: HttpRequest): HttpByteBuf

    override fun isRelevant(ctx: InjectorContext): Boolean {
        return ctx.message.isRequestGet()
    }

    final override fun onRead(ctx: ChannelHandlerContext, buf: ByteBuf): Boolean {
        val request = HttpRequest.parse(buf)
        val response = intercept(ctx, request)

        ctx.writeAndFlush(response.inner)
            .addListener(ChannelFutureListener.CLOSE)
            .addListener { future ->
                val cause = future.cause()
                if (cause == null) {
                    logger.debug("Write successful")
                } else {
                    logger.error("Write failed: $cause")
                    cause.printStackTrace()
                }
            }

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
