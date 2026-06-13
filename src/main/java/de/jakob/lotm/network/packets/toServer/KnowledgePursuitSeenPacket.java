package de.jakob.lotm.network.packets.toServer;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityHandler;
import de.jakob.lotm.abilities.hermit.passives.KnowledgePursuitAbility;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KnowledgePursuitSeenPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KnowledgePursuitSeenPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "knowledge_particle_seen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgePursuitSeenPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new KnowledgePursuitSeenPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KnowledgePursuitSeenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) return;

            // Ability wil apply effects server-side
            KnowledgePursuitAbility ability = (KnowledgePursuitAbility) PassiveAbilityHandler.KNOWLEDGE_PURSUIT.get();
            ability.onParticleSeen(player);
        });
    }
}