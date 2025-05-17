package net.mcbrawls.inject.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import net.mcbrawls.inject.api.Injector;
import net.mcbrawls.inject.api.InjectorContext;
import net.mcbrawls.inject.api.PacketDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Sharable
public abstract class HttpInjector extends Injector {
    private static final List<String> REQUEST_METHODS = List.of("GET ", "POST ", "PUT ", "DELETE ", "PATCH ");

    private final Logger logger = LoggerFactory.getLogger("HttpInjector " + hashCode());

    public abstract HttpByteBuf intercept(ChannelHandlerContext ctx, HttpRequest request);

    /**
     * Predicate for matching if this injector is relevant to the given HTTP request.
     * Should only need to be overridden once you are dealing with multiple HTTP injectors.
     *
     * @param ctx     the context.
     * @param request the HTTP request.
     * @return true if it is relevant and should be handled by this injector, false if not.
     */
    public boolean isRelevant(InjectorContext ctx, HttpRequest request) {
        return true;
    }

    @Override
    public final boolean isRelevant(InjectorContext ctx, PacketDirection direction) {
        ByteBuf byteBuf = ctx.message();
        if (!isHttp(byteBuf)) {
            return false;
        }

        HttpRequest request;
        try {
            request = HttpRequest.parse(byteBuf);
        } catch (IOException exception) {
            logger.error("failed parsing HTTP request: {}", exception.getMessage());
            return false;
        }

        return isRelevant(ctx, request);
    }

    @Override
    public boolean onRead(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        HttpRequest request = HttpRequest.parse(buf);
        HttpByteBuf response = intercept(ctx, request);
        if (response == null) {
            return false;
        }

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
        if (method.length() > buf.capacity()) {
            return false;
        }

        for (int i = 0; i < method.length(); i++) {
            char charAt = method.charAt(i);
            int byteAt = buf.getUnsignedByte(i);
            if (charAt != byteAt) {
                return false;
            }
        }
        return true;
    }

    private boolean isHttp(ByteBuf buf) {
        for (String methodType : REQUEST_METHODS) {
            if (isRequestMethod(buf, methodType)) {
                return true;
            }
        }

        return false;
    }
}
