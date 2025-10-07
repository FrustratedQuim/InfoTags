/*
 * Отмена рендера TextDisplay на случай использования такового вместо дефолтных ников на сервере.
 */

package com.ratger.mixin.client;

import com.ratger.OnPlayerFocus;
import com.ratger.TextDisplayManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DisplayEntityRenderer.TextDisplayEntityRenderer;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextDisplayEntityRenderer.class)
public class TextDisplayRendererMixin {

    @Unique
    private static TextDisplayEntity lastTextDisplay = null;

    // Для вытягивания координат из объекта
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/decoration/DisplayEntity$TextDisplayEntity;Lnet/minecraft/client/render/entity/state/TextDisplayEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private void storeEntity(TextDisplayEntity entity, TextDisplayEntityRenderState state, float tickDelta, CallbackInfo ci) {
        lastTextDisplay = entity;
    }

    // Сам рендер
    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/TextDisplayEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideTextDisplay(TextDisplayEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
        if (lastTextDisplay == null || OnPlayerFocus.lastTarget == null || TextDisplayManager.isOurTextDisplay(lastTextDisplay)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        PlayerEntity player = OnPlayerFocus.lastTarget;
        double dx = player.getX() - lastTextDisplay.getX();
        double dy = lastTextDisplay.getY() - player.getY();
        double dz = player.getZ() - lastTextDisplay.getZ();

        if (dx * dx <= 1.0 && dz * dz <= 1.0 && dy >= 0 && dy <= 5.0) {
            ci.cancel();
        }
    }
}