package com.ratger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class TextDisplayManager {

    public static TextDisplayEntity ourTextDisplay = null;
    private static final String KEY = "infotags_textdisplay";
    public static final Map<String, PlayerData> players = new HashMap<>();

    public static void displayInfo(String name) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        if (OnPlayerFocus.lastTarget == null) {
            client.world.removeEntity(ourTextDisplay.getId(), Entity.RemovalReason.DISCARDED);
            ourTextDisplay = null;
            return;
        }

        if (!players.containsKey(name)) {
            PlayerData data = new PlayerData(0.0, 0, "null");
            players.put(name, data);
            NetworkHandler.sendRequest(name);
        }

        Vec3d pos = OnPlayerFocus.lastTarget.getPos().add(0, 2.2, 0); // Позиция над головой
        PlayerData data = players.get(name);
        String text = String.format(
                "§6Ник: §e%s\n§6Здоровье: §e%.1f\n§6Еда: §e%d\n\n§6Магическое число: §e%s",
                name, data.hp(), data.food(), data.magicCode()
        );

        if (ourTextDisplay == null) {
            ourTextDisplay = new TextDisplayEntity(EntityType.TEXT_DISPLAY, client.world);
            ourTextDisplay.setText(Text.of(text));
            ourTextDisplay.setPosition(pos.x, pos.y, pos.z);

            ourTextDisplay.setDisplayFlags((byte) (ourTextDisplay.getDisplayFlags() | TextDisplayEntity.SHADOW_FLAG)); // Тень шрифта
            ourTextDisplay.setBackground(0);
            ourTextDisplay.setBillboardMode(BillboardMode.CENTER); // Постоянный поворот к клиенту

            ourTextDisplay.setCustomName(Text.of(KEY));
            ourTextDisplay.setCustomNameVisible(false);

            client.world.addEntity(ourTextDisplay);
        } else {
            ourTextDisplay.setText(Text.of(text));
            ourTextDisplay.setPosition(pos.x, pos.y, pos.z);
        }
    }

    public static boolean isOurTextDisplay(TextDisplayEntity entity) {
        return entity.hasCustomName() && entity.getCustomName().getString().equals(KEY); // Нул откинется от первого условия
    }
}
