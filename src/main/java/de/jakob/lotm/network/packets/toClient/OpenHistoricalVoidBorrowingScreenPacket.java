package de.jakob.lotm.network.packets.toClient;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.network.packets.handlers.ClientHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record OpenHistoricalVoidBorrowingScreenPacket(List<String> options) implements CustomPacketPayload {
    public static final Type<OpenHistoricalVoidBorrowingScreenPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "open_historical_void_borrowing_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenHistoricalVoidBorrowingScreenPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(java.util.ArrayList::new, ByteBufCodecs.STRING_UTF8),
                    OpenHistoricalVoidBorrowingScreenPacket::options,
                    OpenHistoricalVoidBorrowingScreenPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenHistoricalVoidBorrowingScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().getReceptionSide().isClient()) {
                ClientHandler.handleHistoricalVoidBorrowingScreenPacket(packet);
            }
        });
    }
}