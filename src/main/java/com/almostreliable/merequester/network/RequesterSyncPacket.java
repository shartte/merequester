package com.almostreliable.merequester.network;

import com.almostreliable.merequester.Utils;
import com.almostreliable.merequester.client.abstraction.AbstractRequesterScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequesterSyncPacket(boolean clearData, long requesterId, CompoundTag data) implements CustomPacketPayload {

    static final Type<RequesterSyncPacket> TYPE = new Type<>(Utils.getRL("requester_sync"));

    static final StreamCodec<ByteBuf, RequesterSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, RequesterSyncPacket::clearData,
        ByteBufCodecs.VAR_LONG, RequesterSyncPacket::requesterId,
        ByteBufCodecs.COMPOUND_TAG, RequesterSyncPacket::data,
        RequesterSyncPacket::new
    );

    public static RequesterSyncPacket createClearData() {
        return new RequesterSyncPacket(true, -1, new CompoundTag());
    }

    public static RequesterSyncPacket createInventory(long requesterId, CompoundTag data) {
        return new RequesterSyncPacket(false, requesterId, data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequesterSyncPacket payload, IPayloadContext context) {
        if (Minecraft.getInstance().screen instanceof AbstractRequesterScreen<?> screen) {
            screen.updateFromMenu(payload.clearData, payload.requesterId, payload.data);
        }
    }
}
