package net.mcbrawls.inject.api;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;

/**
 * Context of an injector.
 *
 * @param pipeline The channel pipeline.
 * @param message  The read byte buffer.
 */
public record InjectorContext(ChannelPipeline pipeline, ByteBuf message) {
}