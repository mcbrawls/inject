package net.mcbrawls.inject.api;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * A Netty injector.
 */
@Sharable
public abstract class Injector extends ChannelDuplexHandler {
    /**
     * Predicate for matching if this injector is relevant to the given context.
     *
     * @param ctx       the context.
     * @param direction the direction in which this packet goes.
     * @return true if it is relevant and should be handled by this injector, false if not.
     */
    public boolean isRelevant(InjectorContext ctx, PacketDirection direction) {
        return false;
    }

    /**
     * Gets executed on every channel read.
     *
     * @param ctx The context.
     * @param buf The read byte buffer.
     * @return true if the channel read was handled and should not get
     * delegated to the superclass, false if it should be delegated to the superclass.
     */
    public boolean onRead(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        return false;
    }

    /**
     * Gets executed on every channel write.
     *
     * @param ctx     The context.
     * @param buf     The written byte buffer.
     * @param promise The mutable channel future.
     * @return true if the channel write was handled and should not get
     * delegated to the superclass, false if it should be delegated to the superclass.
     */
    public boolean onWrite(ChannelHandlerContext ctx, ByteBuf buf, ChannelPromise promise) {
        return false;
    }

    // Exists for ease of mixing into
    private ByteBuf extractByteBuf(Object msg) {
        ByteBuf buf = null;
        if (msg instanceof ByteBuf byteBuf) {
            buf = byteBuf;
        }
        return buf;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = extractByteBuf(msg);
        if (buf == null) {
            return;
        }

        InjectorContext context = new InjectorContext(ctx.pipeline(), buf);
        if (!isRelevant(context, PacketDirection.INBOUND)) {
            super.channelRead(ctx, msg);
            return;
        }

        if (!onRead(ctx, buf)) super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = extractByteBuf(msg);
        if (buf == null) {
            return;
        }

        InjectorContext context = new InjectorContext(ctx.pipeline(), buf);
        if (!isRelevant(context, PacketDirection.OUTBOUND)) {
            super.write(ctx, msg, promise);
            return;
        }

        if (!onWrite(ctx, buf, promise)) super.write(ctx, msg, promise);
    }
}
