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

    final override fun isRelevant(ctx: InjectorContext): Boolean {
        val buf = ctx.message
        val byte1 = buf.getUnsignedByte(buf.readerIndex()).toInt()
        val byte2 = buf.getUnsignedByte(buf.readerIndex() + 1).toInt()
        return isHttp(byte1, byte2)
    }

    final override fun onRead(ctx: ChannelHandlerContext, buf: ByteBuf): Boolean {
        val request = HttpRequest.parse(buf)
        val response = intercept(ctx, request)
        ctx.writeAndFlush(response.inner).addListener(ChannelFutureListener.CLOSE)

        return true
    }

    private companion object {
        /**
         * Checks if the first two bytes match an HTTP request type.
         */
        fun isHttp(byte1: Int, byte2: Int): Boolean {
            return  (byte1.toChar() == 'G' && byte2.toChar() == 'E') ||  // GET
                    (byte1.toChar() == 'P' && byte2.toChar() == 'O') ||  // POST
                    (byte1.toChar() == 'P' && byte2.toChar() == 'U') ||  // PUT
                    (byte1.toChar() == 'H' && byte2.toChar() == 'E') ||  // HEAD
                    (byte1.toChar() == 'O' && byte2.toChar() == 'P') ||  // OPTIONS
                    (byte1.toChar() == 'P' && byte2.toChar() == 'A') ||  // PATCH
                    (byte1.toChar() == 'D' && byte2.toChar() == 'E') ||  // DELETE
                    (byte1.toChar() == 'T' && byte2.toChar() == 'R') ||  // TRACE
                    (byte1.toChar() == 'C' && byte2.toChar() == 'O')     // CONNECT
        }
    }
}
