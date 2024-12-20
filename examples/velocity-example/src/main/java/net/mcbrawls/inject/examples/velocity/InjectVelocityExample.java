package net.mcbrawls.inject.examples.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.mcbrawls.inject.api.Injector;
import net.mcbrawls.inject.api.InjectorContext;
import net.mcbrawls.inject.api.PacketDirection;
import net.mcbrawls.inject.velocity.InjectVelocity;

@Plugin(id = "inject-velocity-example")
public final class InjectVelocityExample extends Injector {
    @Inject
    public InjectVelocityExample(ProxyServer server) {
        InjectVelocity.INSTANCE.init(server);
        InjectVelocity.INSTANCE.registerInjector(this);
    }

    @Override
    public boolean isRelevant(InjectorContext ctx, PacketDirection direction) {
        return true;
    }

    @Override
    public boolean onRead(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        System.out.println("Packet received!");
        return super.onRead(ctx, buf);
    }
}
