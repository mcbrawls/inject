package net.mcbrawls.inject.fabric.mixin;

import io.netty.channel.ChannelPipeline;
import net.mcbrawls.inject.fabric.InjectFabric;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.PacketSizeLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "addHandlers", at = @At("TAIL"))
    private static void addHandlers(ChannelPipeline pipeline, NetworkSide side, boolean local, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
        InjectFabric.INSTANCE.injectors.forEach(pipeline::addFirst);
    }
}
