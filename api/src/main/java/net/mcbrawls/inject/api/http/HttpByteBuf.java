package net.mcbrawls.inject.api.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;

/**
 * A custom ByteBuf used for sending HTTP responses using HttpInterceptor.
 */
public class HttpByteBuf {
    private final ByteBuf inner;

    public HttpByteBuf(ByteBuf inner) {
        this.inner = inner;
    }

    /**
     * Writes the status line to the buffer.
     * Format is as following:
     * HTTP/{protocolVersion} {statusCode} {statusMessage} \n
     */
    public void writeStatusLine(String protocolVersion, int statusCode, String statusMessage) {
        inner.writeCharSequence("HTTP/" + protocolVersion + " " + statusCode + " " + statusMessage + "\n", StandardCharsets.US_ASCII);
    }

    /**
     * Writes an HTTP header to the buffer.
     */
    public void writeHeader(String header, String value) {
        inner.writeCharSequence(header + ": " + value + "\n", StandardCharsets.US_ASCII);
    }

    /**
     * Writes text to the buffer.
     */
    public void writeText(String text) {
        inner.writeCharSequence("\n" + text, StandardCharsets.US_ASCII);
    }

    /**
     * Writes a byte array to the buffer.
     */
    public void writeBytes(byte[] bytes) {
        inner.writeCharSequence("\n", StandardCharsets.US_ASCII);
        inner.writeBytes(bytes);
    }

    /**
     * @return the inner byte buf
     */
    public ByteBuf inner() {
        return inner;
    }

    /**
     * Creates a new {@link HttpByteBuf} based off a {@link ChannelHandlerContext}'s allocator.
     * @param ctx the context
     * @return the HTTP byte buf
     */
    public static HttpByteBuf httpBuf(ChannelHandlerContext ctx) {
        return new HttpByteBuf(ctx.alloc().buffer());
    }
}