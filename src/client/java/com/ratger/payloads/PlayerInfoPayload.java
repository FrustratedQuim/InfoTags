package com.ratger.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

// Обработка пакета с информацией полученного от сервера
public record PlayerInfoPayload(String comment, String playerName, double health, int foodLevel) implements CustomPayload {

    public static final Id<PlayerInfoPayload> ID = new Id<>(Identifier.of("datatransfer:playerinfo"));

    public static final PacketCodec<PacketByteBuf, PlayerInfoPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, PlayerInfoPayload::comment,
            PacketCodecs.STRING, PlayerInfoPayload::playerName,
            PacketCodecs.DOUBLE, PlayerInfoPayload::health,
            PacketCodecs.INTEGER, PlayerInfoPayload::foodLevel,
            PlayerInfoPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public record PlayerData(String comment, double health, int foodLevel) {}
}