package net.mcbrawls.inject

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext

/**
 * A Netty injector.
 */
@Sharable
abstract class Injector : ChannelDuplexHandler() {
    /**
     * Predicate for matching if this injector is relevant
     * to the given context.
     * @param ctx The context.
     * @return true if it's relevant, false if not.
     */
    abstract fun isRelevant(ctx: InjectorContext): Boolean

    /**
     * Gets executed on every channel read.
     * @param ctx The context.
     * @return true if the channel read was handled and should not get
     * delegated to the superclass, false if it should be delegated to the superclass.
     */
    abstract fun onRead(ctx: ChannelHandlerContext, buf: ByteBuf): Boolean

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val buf = msg as ByteBuf
        val context = InjectorContext(ctx.pipeline(), buf)
        if (!isRelevant(context)) {
            super.channelRead(ctx, msg)
            return
        }

        val shouldDelegate = !onRead(ctx, buf)

        if (shouldDelegate) super.channelRead(ctx, msg)
    }

    /**
     * Registers this injector.
     */
    fun register() {
        InjectMod.injectors.add(this)
    }
}