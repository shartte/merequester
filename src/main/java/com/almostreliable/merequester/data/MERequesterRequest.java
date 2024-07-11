package com.almostreliable.merequester.data;

import appeng.api.stacks.AEKey;
import com.almostreliable.merequester.requester.status.RequestStatus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record MERequesterRequest(
    boolean state,
    Optional<AEKey> key,
    long amount,
    long batch,
    RequestStatus clientStatus
) {

    public static final Codec<MERequesterRequest> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.BOOL.fieldOf("state").forGetter(MERequesterRequest::state),
        Codec.optionalField("key", AEKey.CODEC, false).forGetter(MERequesterRequest::key),
        Codec.LONG.fieldOf("amount").forGetter(MERequesterRequest::amount),
        Codec.LONG.fieldOf("batch").forGetter(MERequesterRequest::batch),
        RequestStatus.CODEC.fieldOf("client_status").forGetter(MERequesterRequest::clientStatus)
    ).apply(builder, MERequesterRequest::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MERequesterRequest> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, MERequesterRequest::state,
        ByteBufCodecs.optional(AEKey.STREAM_CODEC), MERequesterRequest::key,
        ByteBufCodecs.VAR_LONG, MERequesterRequest::amount,
        ByteBufCodecs.VAR_LONG, MERequesterRequest::batch,
        RequestStatus.STREAM_CODEC, MERequesterRequest::clientStatus,
        MERequesterRequest::new
    );
}
