package de.jakob.lotm.network.packets.toClient;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.hermit.MysticalReenactmentAbility;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SyncUnlockedMythsPacket(List<Integer> unlockedIndices) implements CustomPacketPayload {

    public static final Type<SyncUnlockedMythsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "sync_unlocked_myths"));

    public static final StreamCodec<ByteBuf, SyncUnlockedMythsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
            SyncUnlockedMythsPacket::unlockedIndices,
            SyncUnlockedMythsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncUnlockedMythsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Ability ability = LOTMCraft.abilityHandler.getById("mystical_reenactment_ability");
            if (ability instanceof MysticalReenactmentAbility reenactment) {
                reenactment.setClientUnlockedIndices(packet.unlockedIndices());
            }
        });
    }
}