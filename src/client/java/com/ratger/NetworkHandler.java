package com.ratger;

import com.ratger.payloads.HandshakePayload;
import com.ratger.payloads.PlayerInfoPayload;
import com.ratger.payloads.PlayerInfoRequestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

public class NetworkHandler {
    private static boolean handshakeSuccessful = false;
    private static boolean blockRequests = false;
    private static boolean receivedServerData = false;
    private static boolean initialDataRequested = false;
    private static long joinTime = 0;
    public static final Map<String, PlayerInfoPayload.PlayerData> playerDataCache = new HashMap<>();

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(PlayerInfoPayload.ID, PlayerInfoPayload.CODEC); // Сервер -> Клиент
        PayloadTypeRegistry.playC2S().register(HandshakePayload.ID, HandshakePayload.CODEC); // Клиент -> Сервер
        PayloadTypeRegistry.playC2S().register(PlayerInfoRequestPayload.ID, PlayerInfoRequestPayload.CODEC); // Клиент -> Сервер

        // Обработка пакета с информацией от сервера
        ClientPlayNetworking.registerGlobalReceiver(PlayerInfoPayload.ID, (payload, context) -> {
            playerDataCache.put(payload.playerName(), new PlayerInfoPayload.PlayerData(payload.comment(), payload.health(), payload.foodLevel()));
            receivedServerData = true;
        });
    }

    public static void sendHandshake() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !handshakeSuccessful) {
            ClientPlayNetworking.send(new HandshakePayload("DataTransferHandshake"));
            handshakeSuccessful = true;
            joinTime = System.currentTimeMillis();

            if (!initialDataRequested) {
                initialDataRequested = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {}
                    client.execute(() -> {
                        if (client.player != null) {
                            String playerName = client.player.getName().getString();
                            ClientPlayNetworking.send(new PlayerInfoRequestPayload(playerName));
                        }
                    });
                }).start();
            }
        }
    }


    // 20 секунд даётся на случай пинга + низкого тпс, но не на критичном уровне
    public static void checkHandshakeTimeout() {
        if (!blockRequests && handshakeSuccessful && !receivedServerData) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - joinTime > 20_000) {
                blockRequests = true;
            }
        }
    }

    // Вытягиваем данные из кэша, либо запрашиваем у сервера
    public static void requestPlayerData(String playerName) {
        if (!handshakeSuccessful || blockRequests || playerDataCache.containsKey(playerName)) {
            if (playerDataCache.containsKey(playerName) && MinecraftClient.getInstance().player == null) {
                throw new IllegalStateException("Player is null while cached data exists");
            }
            return;
        }
        String trimmedName = playerName.trim();
        ClientPlayNetworking.send(new PlayerInfoRequestPayload(trimmedName));
    }

    public static void resetHandshakeAndCache() {
        handshakeSuccessful = false;
        blockRequests = false;
        receivedServerData = false;
        initialDataRequested = false;
        playerDataCache.clear();
        joinTime = 0;
    }

    public static boolean isHandshakeSuccessful() {
        return handshakeSuccessful;
    }

    public static boolean isRequestsBlocked() {
        return blockRequests;
    }
}
