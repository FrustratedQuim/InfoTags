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

import java.util.Map;
import java.util.WeakHashMap;

// Обработка удаления TextDisplay в случае замены дефолтного ника игрока на таковой
@Mixin(TextDisplayEntityRenderer.class)
public class TextDisplayRendererMixin {

    @Unique
    private static final Map<TextDisplayEntityRenderState, TextDisplayEntity> entityMap = new WeakHashMap<>();

    // Для получения координат TextDisplay
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/decoration/DisplayEntity$TextDisplayEntity;Lnet/minecraft/client/render/entity/state/TextDisplayEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private void storeEntity(TextDisplayEntity entity, TextDisplayEntityRenderState state, float tickDelta, CallbackInfo ci) {
        entityMap.put(state, entity);
    }

    // Удаление
    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/TextDisplayEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideTextDisplay(
            TextDisplayEntityRenderState state,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            float tickDelta,
            CallbackInfo ci
    ) {
        TextDisplayEntity entity = entityMap.get(state);
        if (entity == null) return;

        if (TextDisplayManager.isOurTextDisplay(entity)) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        for (PlayerEntity player : client.world.getPlayers()) {
            if (OnPlayerFocus.focusedPlayerNames.contains(player.getName().getString())) {
                double dx = Math.abs(player.getX() - entity.getX());
                double dy = entity.getY() - player.getY();
                double dz = Math.abs(player.getZ() - entity.getZ());

                if (dx <= 1.0 && dz <= 1.0 && dy >= 0 && dy <= 5.0) {
                    ci.cancel();
                    break;
                }
            }
        }
    }
}
