package de.jakob.lotm.network.packets.toClient;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.hermit.passives.KnowledgePursuitAbility;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toServer.ConsumeKnowledgeClusterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestKnowledgeClusterPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestKnowledgeClusterPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "request_knowledge_cluster"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestKnowledgeClusterPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new RequestKnowledgeClusterPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestKnowledgeClusterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null)
                return;

            UUID uuid = Minecraft.getInstance().player.getUUID();

            // Only reply if we actually have a cluster to consume
            if (KnowledgePursuitAbility.hasActiveClusters(uuid)) {
                KnowledgePursuitAbility.consumeOldestCluster(uuid);
                PacketHandler.sendToServer(new ConsumeKnowledgeClusterPacket());
            }
            // If no clusters, send nothing — server timeout handles the failure message
        });
    }
}