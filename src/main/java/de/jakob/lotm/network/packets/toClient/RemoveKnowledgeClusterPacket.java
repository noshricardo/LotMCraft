package de.jakob.lotm.network.packets.toClient;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.hermit.passives.KnowledgePursuitAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveKnowledgeClusterPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RemoveKnowledgeClusterPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "remove_knowledge_cluster"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveKnowledgeClusterPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new RemoveKnowledgeClusterPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RemoveKnowledgeClusterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null)
                return;
            KnowledgePursuitAbility.consumeOldestCluster(
                    Minecraft.getInstance().player.getUUID());
        });
    }
}