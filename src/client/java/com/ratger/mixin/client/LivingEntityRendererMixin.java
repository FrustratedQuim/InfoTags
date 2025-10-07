/*
 * Скрытие дефолтного ника наблюдаемого игрока.
 */

package com.ratger.mixin.client;

import com.ratger.OnPlayerFocus;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(
            method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideNameTag(LivingEntity entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (
                OnPlayerFocus.lastTarget != null &&
                entity instanceof PlayerEntity player &&
                player == OnPlayerFocus.lastTarget
        ) {
            cir.setReturnValue(false);
        }
    }
}