package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.attachments.CopiedAbilityComponent;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.RequestKnowledgeClusterPacket;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.CopiedAbilityHelper;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.VectorUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InfoAuthorityAbility extends SelectableAbility {

    private static final Set<UUID> pendingClusterConsume = new HashSet<>();

    public InfoAuthorityAbility(String id) {
        super(id, 1f);
        canBeCopied = false;
        canBeUsedByNPC = false;
        this.cannotBeStolen = true;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 2));
    }

    @Override
    public float getSpiritualityCost() {
        return 0;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.info_authority.integrate",
                "ability.lotmcraft.info_authority.access"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int selectedAbility) {
        if (selectedAbility == 0)
            integrate(level, entity);
        else
            openCopiedAbilityWheel(level, entity);
    }

    private void openCopiedAbilityWheel(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel) || !(entity instanceof ServerPlayer player))
            return;
        CopiedAbilityHelper.openCopiedAbilityWheel(player);
    }

    // Called by ConsumeKnowledgeClusterPacket, confirmed it had a cluster
    public static void onClusterConsumed(ServerPlayer player) {
        if (!pendingClusterConsume.remove(player.getUUID()))
            return;

        // Pick a random S2-9 copiable ability from any pathway
        var allAbilities = LOTMCraft.abilityHandler.getAbilities().stream()
                .filter(a -> a.canBeCopied)
                .filter(a -> a.lowestSequenceUsable() >= 2
                        && a.lowestSequenceUsable() <= 9)
                .toList();

        if (allAbilities.isEmpty()) {
            AbilityUtil.sendActionBar(player,
                    Component.literal("No data available to integrate...")
                            .withColor(0xFF8ff4ff));
            return;
        }

        Ability chosen = allAbilities.get(
                new java.util.Random().nextInt(allAbilities.size()));

        CopiedAbilityHelper.addAbility(player,
                new CopiedAbilityComponent.CopiedAbilityData(
                        chosen.getId(),
                        "integrated",
                        -1,
                        null
                ));

        AbilityUtil.sendActionBar(player,
                Component.literal("Integrated: ")
                        .append(chosen.getName())
                        .withColor(0xFF8ff4ff));

        player.level().playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1.0f, 1.5f);
    }

    // Integrate checks client if it has a cluster, grants random ability on repl
    private void integrate(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        if (!(entity instanceof ServerPlayer player))
            return;

        if (pendingClusterConsume.contains(player.getUUID())) {
            AbilityUtil.sendActionBar(player,
                    Component.literal("Data analysis pending...")
                            .withColor(0xFF8ff4ff));
            return;
        }

        Vec3 pos = VectorUtil.getRelativePosition(
                entity.getEyePosition().add(0, -.4, 0),
                new Vec3(entity.getLookAngle().x, 0, entity.getLookAngle().z).normalize(),
                1.2, 0, -.4);

        pendingClusterConsume.add(player.getUUID());

        // Ask client to confirm and consume a cluster
        PacketHandler.sendToPlayer(player, new RequestKnowledgeClusterPacket());

        // Timeout: if client doesn't reply in 2 seconds, no clusters available
        ServerScheduler.scheduleDelayed(40, () -> {
            if (pendingClusterConsume.remove(player.getUUID())) {
                AbilityUtil.sendActionBar(player,
                        Component.literal("No data available to integrate...")
                                .withColor(0xFFff4444));
                ParticleUtil.spawnParticles(serverLevel, ParticleTypes.ENCHANT,
                        pos, 20, 0.3, 0.02);
            }
        }, serverLevel);
    }
}