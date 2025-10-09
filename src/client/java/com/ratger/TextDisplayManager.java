package com.ratger;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class TextDisplayManager {

    public static double lineHeight = 0.25;
    public static float lineScale = 0.0235f;

    public static String[] ourTextLines = null;
    public static Vec3d ourTextPosition = null;
    public static final Map<String, PlayerData> players = new HashMap<>();

    public static void init() {
        renderText();
    }

    public static void displayInfo(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        if (OnPlayerFocus.lastTarget == null) {
            ourTextLines = null;
            ourTextPosition = null;
            return;
        }

        if (!players.containsKey(name)) {
            PlayerData data = new PlayerData(0.0, 0, "null");
            players.put(name, data);
            NetworkHandler.sendRequest(name);
        }

        ourTextPosition = OnPlayerFocus.lastTarget.getPos().add(0, 2.2, 0); // Позиция над головой
        PlayerData data = players.get(name);

        ourTextLines = new String[]{
                "§6Магическое число: §e" + data.magicCode(),
                "§6Здоровье: §e" + data.hp(),
                "§6Еда: §e" + data.food(),
                "§6Ник: §e" + name
        };
    }

    public static void renderText() {

        WorldRenderEvents.LAST.register(context -> {
            if (ourTextLines == null) return;

            MinecraftClient client = MinecraftClient.getInstance();
            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
            TextRenderer textRenderer = client.textRenderer;

            // Мы используем низкоуровневый draw вместо drawString, чтобы передать параметр true для включения тени.
            // Функция drawString по умолчанию имеет отключённую тень текста, что крайне неудобно. По же этой причине
            // вычисления из drawString были перенесены прямо сюда.
            for (int i = 0; i < ourTextLines.length; ++i) {
                double yOffset = lineHeight * i;
                assert matrices != null;
                matrices.push();
                matrices.translate(ourTextPosition.x - client.gameRenderer.getCamera().getPos().x,
                        ourTextPosition.y + yOffset - client.gameRenderer.getCamera().getPos().y + 0.07F,
                        ourTextPosition.z - client.gameRenderer.getCamera().getPos().z);
                matrices.multiply(client.gameRenderer.getCamera().getRotation());
                matrices.scale(lineScale, -lineScale, lineScale); // Размер текста
                float g = -textRenderer.getWidth(ourTextLines[i]) / 2.0F;
                textRenderer.draw(
                        ourTextLines[i],
                        g,
                        0.0F,
                        0,
                        true,
                        matrices.peek().getPositionMatrix(),
                        vertexConsumers,
                        TextRenderer.TextLayerType.NORMAL,
                        0,
                        15728880
                );
                matrices.pop();
            }
        });
    }
}
