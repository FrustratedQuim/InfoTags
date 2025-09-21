package com.ratger;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OnPlayerFocus {

    public static Set<String> focusedPlayerNames = new HashSet<>();
    public static Map<String, Long> lastLookedTimes = new HashMap<>();

    public static void startListening() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                focusedPlayerNames.clear();
                lastLookedTimes.clear();
                TextDisplayManager.update();
                return;
            }

            HitResult target = client.crosshairTarget; // Дёргает цель взгляда, удар не обязателен
            if (target != null && target.getType() == HitResult.Type.ENTITY && target instanceof EntityHitResult entityHit) {
                if (entityHit.getEntity() instanceof PlayerEntity targetPlayer) {
                    String targetName = targetPlayer.getName().getString();
                    if (!focusedPlayerNames.contains(targetName)) {
                        focusedPlayerNames.add(targetName);
                        lastLookedTimes.put(targetName, System.currentTimeMillis());
                        NetworkHandler.requestPlayerData(targetName);
                    }
                    TextDisplayManager.update();
                    return;
                }
            }

            long currentTime = System.currentTimeMillis();
            focusedPlayerNames.removeIf(name -> {
                if (client.world == null) {
                    return false;
                }

                if (client.world.getPlayers().stream().noneMatch(p -> p.getName().getString().equals(name))) {
                    lastLookedTimes.remove(name);
                    return true;
                }

                Long lastLooked = lastLookedTimes.get(name);
                if (lastLooked != null && currentTime - lastLooked > 4000) {
                    lastLookedTimes.remove(name);
                    return true;
                }
                return false;
            });

            TextDisplayManager.update();
        });
    }
}