package com.almostreliable.merequester.network;

import com.almostreliable.merequester.Utils;
import com.almostreliable.merequester.requester.abstraction.AbstractRequesterMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DragAndDropPacket(
    long requesterId,
    int requestIndex,
    ItemStack item
) implements CustomPacketPayload {

    public static final Type<DragAndDropPacket> TYPE = new Type<>(Utils.getRL("drag_and_drop"));

    static final StreamCodec<RegistryFriendlyByteBuf, DragAndDropPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, DragAndDropPacket::requesterId,
        ByteBufCodecs.INT, DragAndDropPacket::requestIndex,
        ItemStack.OPTIONAL_STREAM_CODEC, DragAndDropPacket::item,
        DragAndDropPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DragAndDropPacket payload, IPayloadContext context) {
        var player = context.player();
        if (player instanceof ServerPlayer serverPlayer && player.containerMenu instanceof AbstractRequesterMenu requester) {
            requester.applyDragAndDrop(serverPlayer, payload.requestIndex, payload.requesterId, payload.item);
        }
    }
}
