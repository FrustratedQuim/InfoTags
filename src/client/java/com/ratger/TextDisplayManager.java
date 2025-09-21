package com.ratger;

import com.ratger.payloads.PlayerInfoPayload; // Добавлен импорт
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TextDisplayManager {
    private static final Map<String, DisplayEntity.TextDisplayEntity> displayedTexts = new HashMap<>();
    public static final String KEY = "infotags_textdisplay"; // Ключ для отличия TextDisplay

    public static void update() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            displayedTexts.clear();
            return;
        }

        Set<String> focusedPlayers = OnPlayerFocus.focusedPlayerNames;

        // Удаление неактуальных
        displayedTexts.entrySet().removeIf(entry -> {
            if (!focusedPlayers.contains(entry.getKey())) {
                client.world.removeEntity(entry.getValue().getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
                return true;
            }
            return false;
        });

        // Спавн новых
        for (String focused : focusedPlayers) {
            client.world.getPlayers().stream()
                    .filter(p -> p.getName().getString().equals(focused))
                    .findFirst()
                    .ifPresent(targetPlayer -> {

                        Vec3d pos = targetPlayer.getPos().add(0, 2.2, 0); // Позиция над головой
                        DisplayEntity.TextDisplayEntity textDisplay = displayedTexts.get(focused);

                        String finalString;

                        PlayerInfoPayload.PlayerData data = NetworkHandler.playerDataCache.get(focused);
                        if (data != null) {
                            finalString = String.format("§6Ник: §e%s\n§6Здоровье: §e%.1f\n§6Еда: §e%d\n\n§6Комментарий: §e%s",
                                    focused, data.health(), data.foodLevel(), data.comment());
                        } else {
                            String value = NetworkHandler.isHandshakeSuccessful() && !NetworkHandler.isRequestsBlocked() ? "Ожидание..." : "null";
                            finalString = String.format("§6Ник: §e%s\n§6Здоровье: §e%s\n§6Еда: §e%s",
                                    focused, value, value);
                        }

                        if (textDisplay == null) {
                            textDisplay = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, client.world);
                            textDisplay.setText(Text.of(finalString));
                            textDisplay.setPosition(pos.x, pos.y, pos.z);

                            textDisplay.setDisplayFlags((byte) (textDisplay.getDisplayFlags() | DisplayEntity.TextDisplayEntity.SHADOW_FLAG)); // Тень шрифта
                            textDisplay.setBackground(0); // Прозрачный фон
                            textDisplay.setBillboardMode(DisplayEntity.BillboardMode.CENTER); // Поворот к игроку

                            textDisplay.setCustomName(Text.of(KEY)); // Не текст, а именно имя объекта для отличия
                            textDisplay.setCustomNameVisible(false);

                            client.world.addEntity(textDisplay);
                            displayedTexts.put(focused, textDisplay);
                        } else {
                            textDisplay.setText(Text.of(finalString));
                            textDisplay.setPosition(pos.x, pos.y, pos.z);
                        }
                    });
        }
    }

    public static boolean isOurTextDisplay(DisplayEntity.TextDisplayEntity entity) {
        return entity.hasCustomName() && KEY.equals(Objects.requireNonNull(entity.getCustomName()).getString());
    }
}