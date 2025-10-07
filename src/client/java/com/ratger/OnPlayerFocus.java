package com.ratger;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;

public class OnPlayerFocus {

    public static PlayerEntity lastTarget;
    public static String lastTargetName = "";
    public static boolean active = true;

    public static void startListening() {
        active = true;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!active || client == null || client.player == null || client.interactionManager == null) return;
            if (client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) return;

            HitResult target = client.crosshairTarget;

            PlayerEntity player = null;
            if (target instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof PlayerEntity p) {
                player = p;
            }

            if (player != lastTarget) {
                lastTarget = player;
                lastTargetName = player != null ? player.getName().getString() : "";
                TextDisplayManager.displayInfo(lastTargetName);
                return;
            }

            if (player != null) {
                TextDisplayManager.displayInfo(lastTargetName);
            }
        });
    }

    public static void stopListening() {
        active = false;
        lastTarget = null;
        lastTargetName = "";
    }
}