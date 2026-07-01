package de.jakob.lotm.network.packets.toClient;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.network.packets.handlers.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncControllingDataPacket(boolean isControlling, CompoundTag bodyEntity, int entityId) implements CustomPacketPayload {
    
    public static final Type<SyncControllingDataPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "sync_controlling_data"));
    
    public static final StreamCodec<ByteBuf, SyncControllingDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SyncControllingDataPacket::isControlling,
            ByteBufCodecs.COMPOUND_TAG.map(
                    tag -> tag,
                    tag -> tag == null ? new CompoundTag() : tag
            ),
            SyncControllingDataPacket::bodyEntity,
            ByteBufCodecs.INT,
            SyncControllingDataPacket::entityId,
        SyncControllingDataPacket::new
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(SyncControllingDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                ClientHandler.handleControllingDataPacket(packet);
            }
        });
    }
}