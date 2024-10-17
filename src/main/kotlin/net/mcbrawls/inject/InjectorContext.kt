package net.mcbrawls.inject

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelPipeline

data class InjectorContext(val pipeline: ChannelPipeline, val message: ByteBuf)