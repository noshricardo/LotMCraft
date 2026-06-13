package de.jakob.lotm.network.packets.toServer;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.hermit.InfoAuthorityAbility;
import de.jakob.lotm.abilities.hermit.passives.KnowledgePursuitAbility;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConsumeKnowledgeClusterPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConsumeKnowledgeClusterPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "consume_knowledge_cluster"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeKnowledgeClusterPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new ConsumeKnowledgeClusterPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConsumeKnowledgeClusterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player))
                return;
            // Server confirms receipt and triggers the actual ability grant
            InfoAuthorityAbility.onClusterConsumed(player);
        });
    }
}