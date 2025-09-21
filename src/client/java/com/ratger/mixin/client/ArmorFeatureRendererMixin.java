package com.ratger.mixin.client;

import com.ratger.OnPlayerFocus;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Обработка скрытия брони
@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <S extends BipedEntityRenderState>
    void hideArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, S state, float f1, float f2, CallbackInfo ci) {
        if (state instanceof PlayerEntityRenderState playerState && OnPlayerFocus.focusedPlayerNames.contains(playerState.name)) {
            ci.cancel();
        }
    }
}
