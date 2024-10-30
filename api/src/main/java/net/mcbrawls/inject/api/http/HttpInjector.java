package net.mcbrawls.inject.api.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import net.mcbrawls.inject.api.Injector;
import net.mcbrawls.inject.api.InjectorContext;
import net.mcbrawls.inject.api.PacketDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public abstract class HttpInjector extends Injector {
    private final Logger logger = LoggerFactory.getLogger("HttpInjector " + hashCode());

    public abstract HttpByteBuf intercept(ChannelHandlerContext ctx, HttpRequest request);

    @Override
    public boolean isRelevant(InjectorContext ctx, PacketDirection direction) {
        return isRequestGet(ctx.message());
    }

    @Override
    public boolean onRead(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        HttpRequest request = HttpRequest.parse(buf);
        HttpByteBuf response = intercept(ctx, request);

        ctx.writeAndFlush(response.inner())
            .addListener(ChannelFutureListener.CLOSE)
            .addListener(future -> {
                Throwable cause = future.cause();
                if (cause == null) {
                    logger.debug("Write successful");
                } else {
                    logger.error("Write failed: {}", String.valueOf(cause));
                    //noinspection CallToPrintStackTrace
                    cause.printStackTrace();
                }
            });

        return true;
    }

    private boolean isRequestMethod(ByteBuf buf, @SuppressWarnings("SameParameterValue") String method) {
        for (int i = 0; i < method.length(); i++) {
            char charAt = method.charAt(i);
            int byteAt = buf.getUnsignedByte(i);
            if (charAt != byteAt) {
                return false;
            }
        }
        return true;
    }

    private boolean isRequestGet(ByteBuf buf) {
        return isRequestMethod(buf, "GET ");
    }
}