package com.ratger;

import com.ratger.payloads.DataTransferPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NetworkHandler {

    private static boolean isSuccess = false;

    public static void init() {
        PayloadTypeRegistry.playC2S().register(DataTransferPayload.ID, DataTransferPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(DataTransferPayload.ID, DataTransferPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(DataTransferPayload.ID, (payload, context) ->
                manageAnswer(payload.content()
          )
        );
    }

    public static void sendHandshake() {
        if (!ClientPlayNetworking.canSend(DataTransferPayload.ID)) {
            sendMessage("absent");
            return;
        }

        isSuccess = false;

        ClientPlayNetworking.send(new DataTransferPayload("FirstRequest"));

        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {}
            if (!isSuccess) {
                sendMessage("timeout");
            }
        }).start();
    }

    public static void sendRequest(String name) {
        ClientPlayNetworking.send(new DataTransferPayload("getData;" + name));
        System.out.println("getData;" + name);
    }

    public static void manageAnswer(String content) {
        if (content.equals("success")) {
            isSuccess = true;
            OnPlayerFocus.startListening();
            sendMessage("yes");
        } else if (content.equals("no_permission")) {
            isSuccess = true;
            sendMessage("no");
        } else if (content.startsWith("giveData")) {
            String[] data = content.split(";");
            TextDisplayManager.players.put(data[1], new PlayerData(Double.parseDouble(data[2]), Integer.parseInt(data[3]), data[4]));
        }
    }

    private static void sendMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        MutableText tempMessage = Text.literal("");

        switch (message) {
            case "yes" -> {
                MutableText part1 = Text.literal("▍ ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
                MutableText part2 = Text.literal("InfoTags получил разрешение.")
                        .setStyle(Style.EMPTY.withColor(0x00FF40));
                tempMessage = part1.append(part2);
            }
            case "no" -> {
                MutableText part1 = Text.literal("▍ ").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
                MutableText part2 = Text.literal("InfoTags получил отказ. Недостаточно прав.")
                        .setStyle(Style.EMPTY.withColor(0xFF1500));
                tempMessage = part1.append(part2);
            }
            case "absent" -> {
                MutableText part1 = Text.literal("▍ ").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
                MutableText part2 = Text.literal("Сервер не поддерживает InfoTags.")
                        .setStyle(Style.EMPTY.withColor(0xFF1500));
                tempMessage = part1.append(part2);
            }
            case "timeout" -> {
                MutableText part1 = Text.literal("▍ ").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
                MutableText part2 = Text.literal("InfoTags не получил ответ от сервера.")
                        .setStyle(Style.EMPTY.withColor(0xFF1500));
                tempMessage = part1.append(part2);
            }
        }

        MutableText finalMessage = tempMessage;
        client.execute(() -> client.player.sendMessage(finalMessage, false));
    }
}
