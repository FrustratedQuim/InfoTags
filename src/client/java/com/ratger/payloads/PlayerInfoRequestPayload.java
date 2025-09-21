package com.ratger.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

// Обработка пакета запроса к серверу за получением информации
public record PlayerInfoRequestPayload(String playerName) implements CustomPayload {

    public static final Id<PlayerInfoRequestPayload> ID = new Id<>(Identifier.of("datatransfer:playerinfo_request"));

    public static final PacketCodec<PacketByteBuf, PlayerInfoRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.xmap(
                    String::trim,
                    String::trim
            ),
            PlayerInfoRequestPayload::playerName,
            PlayerInfoRequestPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}