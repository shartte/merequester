package com.almostreliable.merequester.network;

import com.almostreliable.merequester.Utils;
import com.almostreliable.merequester.requester.abstraction.AbstractRequesterMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestUpdatePacket(
    UpdateType updateType,
    long requesterId,
    int requestIndex,
    boolean state,
    long amount,
    long batch
) implements CustomPacketPayload {

    static final Type<RequestUpdatePacket> TYPE = new Type<>(Utils.getRL("request_update"));

    static final StreamCodec<FriendlyByteBuf, RequestUpdatePacket> STREAM_CODEC = StreamCodec.of(
        RequestUpdatePacket::encode,
        RequestUpdatePacket::decode
    );

    public RequestUpdatePacket(long requesterId, int requestIndex, boolean state) {
        this(UpdateType.STATE, requesterId, requestIndex, state, 0, 0);
    }

    public RequestUpdatePacket(long requesterId, int requestIndex, long amount, long batch) {
        this(UpdateType.NUMBERS, requesterId, requestIndex, false, amount, batch);
    }

    private static void encode(FriendlyByteBuf buffer, RequestUpdatePacket payload) {
        buffer.writeLong(payload.requesterId);
        buffer.writeVarInt(payload.requestIndex);

        buffer.writeVarInt(payload.updateType.ordinal());
        if (payload.updateType == UpdateType.STATE) {
            buffer.writeBoolean(payload.state);
        } else if (payload.updateType == UpdateType.NUMBERS) {
            buffer.writeLong(payload.amount);
            buffer.writeLong(payload.batch);
        } else {
            throw new IllegalStateException("Unknown update type: " + payload.updateType);
        }
    }

    private static RequestUpdatePacket decode(FriendlyByteBuf buffer) {
        var id = buffer.readLong();
        var index = buffer.readVarInt();

        var type = UpdateType.values()[buffer.readVarInt()];
        if (type == UpdateType.STATE) {
            return new RequestUpdatePacket(id, index, buffer.readBoolean());
        }
        if (type == UpdateType.NUMBERS) {
            return new RequestUpdatePacket(id, index, buffer.readLong(), buffer.readLong());
        }
        throw new IllegalStateException("Unknown update type: " + type);
    }

    public static void handle(RequestUpdatePacket payload, IPayloadContext context) {
        if (context.player().containerMenu instanceof AbstractRequesterMenu requester) {
            switch (payload.updateType) {
                case STATE -> requester.updateRequesterState(payload.requesterId, payload.requestIndex, payload.state);
                case NUMBERS -> requester.updateRequesterNumbers(payload.requesterId, payload.requestIndex, payload.amount, payload.batch);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private enum UpdateType {
        STATE, NUMBERS
    }
}
