/**
 * На данном этапе проще работать с одной структурой пакета, укладывая всё в 1 контент параметр.
 * Общий формат: "param1;param2;param3;..."
 */

package com.ratger.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DataTransferPayload(String content) implements CustomPayload {

    public static final Id<DataTransferPayload> ID = new Id<>(Identifier.of("datatransfer", "main"));

    public static final PacketCodec<PacketByteBuf, DataTransferPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.xmap(String::trim, String::trim),
            DataTransferPayload::content,
            DataTransferPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}