package net.mcbrawls.inject.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.mcbrawls.inject.api.Injector;
import net.minecraft.network.OpaqueByteBufHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Injector.class)
public class InjectorMixin {
    @ModifyReturnValue(method = "extractByteBuf", at = @At("TAIL"), remap = false)
    private ByteBuf injected(ByteBuf original, Object msg) {
        if (msg instanceof OpaqueByteBufHolder(ByteBuf contents)) {
            return ByteBufUtil.ensureAccessible(contents);
        }

        return original;
    }
}
