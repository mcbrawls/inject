package net.mcbrawls.inject.http

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.nio.charset.StandardCharsets

/**
 * A custom [ByteBuf] used for sending HTTP responses using [HttpInterceptor].
 * @param inner The inner byte buffer.
 */
class HttpByteBuf(val inner: ByteBuf) {
    /**
     * Writes the status line to the buffer.
     * Format is as following:
     * ```
     * HTTP/{protocolVersion} {statusCode} {statusMessage} \n
     * ```
     */
    fun writeStatusLine(protocolVersion: String, statusCode: Int, statusMessage: String) {
        inner.writeCharSequence("HTTP/$protocolVersion $statusCode $statusMessage\n", StandardCharsets.US_ASCII)
    }

    /**
     * Writes an HTTP header to the buffer.
     */
    fun writeHeader(header: String, value: String) {
        inner.writeCharSequence("$header: $value\n", StandardCharsets.US_ASCII)
    }

    /**
     * Writes text to the buffer.
     */
    fun writeText(text: String) {
        inner.writeCharSequence("\n" + text, StandardCharsets.US_ASCII)
    }

    /**
     * Writes a byte array to the buffer.
     */
    fun writeBytes(bytes: ByteArray) {
        inner.writeCharSequence("\n", StandardCharsets.US_ASCII)
        inner.writeBytes(bytes)
    }
}

fun ChannelHandlerContext.httpBuffer() = HttpByteBuf(alloc().buffer())
inline fun ChannelHandlerContext.buildHttpBuffer(block: HttpByteBuf.() -> Unit) = httpBuffer().apply(block)