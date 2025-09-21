package com.ratger.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

// Обработка пакета для соединения клиента с сервером
public record HandshakePayload(String handshakeData) implements CustomPayload {

    public static final Id<HandshakePayload> ID = new Id<>(Identifier.of("datatransfer:handshake"));

    public static final PacketCodec<PacketByteBuf, HandshakePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.xmap(
                    String::trim,
                    String::trim
            ),
            HandshakePayload::handshakeData,
            HandshakePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}