package net.mcbrawls.inject.examples.fabric;

import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.mcbrawls.inject.fabric.InjectFabric;
import net.mcbrawls.inject.http.HttpByteBuf;
import net.mcbrawls.inject.http.HttpInjector;
import net.mcbrawls.inject.http.HttpRequest;

public class InjectHttpExample implements DedicatedServerModInitializer {
    private static class MyEpicHttpInjector extends HttpInjector {
        @Override
        public HttpByteBuf intercept(ChannelHandlerContext ctx, HttpRequest request) {
            HttpByteBuf buf = HttpByteBuf.httpBuf(ctx);
            buf.writeStatusLine("1.1", 200, "OK");
            buf.writeText("Hello, from Minecraft!");
            return buf;
        }
    }

    @Override
    public void onInitializeServer() {
        InjectFabric.INSTANCE.registerInjector(new MyEpicHttpInjector());
    }
}
