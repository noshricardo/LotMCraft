package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.attachments.ParasitationComponent;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class DreamTraversalAbility extends SelectableAbility {

    private static final HashMap<UUID, UUID> hideMap = new HashMap<>();

    public DreamTraversalAbility(String id) {
        super(id, 1f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 5));
    }

    @Override
    public float getSpiritualityCost() {
        return 60;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.dream_traversal.jump",
                "ability.lotmcraft.dream_traversal.hide",
                "ability.lotmcraft.dream_traversal.guidance"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch (abilityIndex) {
            case 0 -> jump(level, entity);
            case 1 -> hide(level, entity);
            case 2 -> guidance(level, entity);
        }
    }

    private boolean requiresAsleep(LivingEntity entity) {
        return BeyonderData.getSequence(entity) > 3;
    }


    private void jump(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 200, 2);

        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.no_target").withColor(0xFFff124d));
            return;
        }

        if (requiresAsleep(entity) && !target.hasEffect(ModEffects.ASLEEP)) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.must_be_asleep").withColor(0xFFff124d));
            return;
        }

        // If currently hiding, jump to new host without cancelling hide
        if (hideMap.containsKey(entity.getUUID())) {
            Entity oldHostEntity = serverLevel.getEntity(hideMap.get(entity.getUUID()));
            if (oldHostEntity instanceof LivingEntity oldHost) {
                ParasitationComponent oldParasite = oldHost.getData(ModAttachments.PARASITE_COMPONENT);
                oldParasite.setParasited(false);
                oldParasite.setParasiteUUID(null);
            }

            hideMap.put(entity.getUUID(), target.getUUID());
            ParasitationComponent newParasite = target.getData(ModAttachments.PARASITE_COMPONENT);
            newParasite.setParasited(true);
            newParasite.setParasiteUUID(entity.getUUID());
        }

        entity.teleportTo(target.getX(), target.getY(), target.getZ());
    }

    private void hide(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (hideMap.containsKey(entity.getUUID())) {
            cancelHide(serverLevel, entity);
            return;
        }

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 20, 2);

        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.no_target").withColor(0xFFff124d));
            return;
        }

        if (requiresAsleep(entity) && !target.hasEffect(ModEffects.ASLEEP)) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.must_be_asleep").withColor(0xFFff124d));
            return;
        }

        hideMap.put(entity.getUUID(), target.getUUID());

        ParasitationComponent parasitationComponent = target.getData(ModAttachments.PARASITE_COMPONENT);
        parasitationComponent.setParasited(true);
        parasitationComponent.setParasiteUUID(entity.getUUID());

        entity.setInvisible(true);
        if (entity instanceof Player player) {
            player.setBoundingBox(new AABB(
                    player.getX(), player.getY(), player.getZ(),
                    player.getX(), player.getY(), player.getZ()
            ));
            player.onUpdateAbilities();
            player.hurtMarked = true;
        }
    }

    private void cancelHide(ServerLevel serverLevel, LivingEntity entity) {
        if (!hideMap.containsKey(entity.getUUID())) return;

        Entity hostEntity = serverLevel.getEntity(hideMap.get(entity.getUUID()));
        if (hostEntity instanceof LivingEntity host) {
            ParasitationComponent parasitationComponent = host.getData(ModAttachments.PARASITE_COMPONENT);
            parasitationComponent.setParasited(false);
            parasitationComponent.setParasiteUUID(null);
        }

        hideMap.remove(entity.getUUID());

        entity.setInvisible(false);
        entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 0, 0));
        entity.removeEffect(MobEffects.INVISIBILITY);

        if (entity instanceof Player player) {
            player.setBoundingBox(player.getDimensions(player.getPose()).makeBoundingBox(
                    player.getX(), player.getY(), player.getZ()
            ));
            player.onUpdateAbilities();
            player.hurtMarked = true;
        }
    }

    private void guidance(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 200, 2);

        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.no_target").withColor(0xFFff124d));
            return;
        }

        // Guidance always requires the target to be asleep, regardless of sequence
        if (!target.hasEffect(ModEffects.ASLEEP)) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.dream_traversal.must_be_asleep").withColor(0xFFff124d));
            return;
        }

        if (!BeyonderData.isBeyonder(target)) {
            AbilityUtil.sendActionBar(entity,
                    Component.translatable("ability.lotmcraft.dream_traversal.guidance.not_beyonder")
                            .withColor(0xFFffad33));
            return;
        }

        int casterSeq = BeyonderData.getSequence(entity);
        int targetSeq = BeyonderData.getSequence(target);
        String targetPathway = BeyonderData.getPathway(target);
        String pathwayName = BeyonderData.getSequenceName(targetPathway, targetSeq);
        String displayPathway = targetPathway.substring(0, 1).toUpperCase() + targetPathway.substring(1).replace("_", " ");
        //I can make it so they don't do red_priest but instead Red priest (but i dont know how to captilize the second word without making it complex)

        // Same sequence or stronger (lower number) — reveal pathway only (weaker reveals sequence too)
        if (targetSeq <= casterSeq) {
            AbilityUtil.sendActionBar(entity,
                    Component.translatable("ability.lotmcraft.dream_traversal.guidance.pathway_only",
                            displayPathway).withColor(0xFFe3ffff));
        } else {
            AbilityUtil.sendActionBar(entity,
                    Component.translatable("ability.lotmcraft.dream_traversal.guidance.pathway_and_sequence",
                            displayPathway, targetSeq, pathwayName).withColor(0xFFe3ffff));
        }
    }


    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        if (!hideMap.containsKey(entity.getUUID())) return;

        Entity hostEntity = serverLevel.getEntity(hideMap.get(entity.getUUID()));

        boolean seqUnlocked = BeyonderData.getSequence(entity) <= 3;

        if (hostEntity == null || hostEntity.isRemoved() || !(hostEntity instanceof LivingEntity host)
                || !host.isAlive() || (!seqUnlocked && !host.hasEffect(ModEffects.ASLEEP))) {

            if (hostEntity instanceof LivingEntity host2) {
                ParasitationComponent parasitationComponent = host2.getData(ModAttachments.PARASITE_COMPONENT);
                parasitationComponent.setParasited(false);
                parasitationComponent.setParasiteUUID(null);
            }

            hideMap.remove(entity.getUUID());

            entity.setInvisible(false);
            entity.removeEffect(MobEffects.INVISIBILITY);

            if (entity instanceof Player player) {
                player.setBoundingBox(player.getDimensions(player.getPose()).makeBoundingBox(
                        player.getX(), player.getY(), player.getZ()
                ));
                player.onUpdateAbilities();
                player.hurtMarked = true;
            }
            return;
        }

        entity.setInvisible(true);
        entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false, false));

        Vec3 hostPos = host.position();
        Vec3 floatPos = hostPos.add(0, host.getBbHeight() + 0.3, 0);
        entity.teleportTo(floatPos.x, floatPos.y, floatPos.z);
        entity.setDeltaMovement(Vec3.ZERO);

        if (entity instanceof Player player) {
            player.setBoundingBox(new AABB(
                    player.getX(), player.getY(), player.getZ(),
                    player.getX(), player.getY(), player.getZ()
            ));
            player.hurtMarked = true;
        }
    }

    public static boolean isHiding(UUID uuid) {
        return hideMap.containsKey(uuid);
    }
}