package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.entity.ModEntities;
import de.jakob.lotm.entity.custom.AvatarEntity;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class FalseInfoAbility extends Ability {

    private static final DustParticleOptions purpleDust = new DustParticleOptions(
            new Vector3f(0.5f, 0.2f, 0.7f), 1.2f
    );

    public FalseInfoAbility(String id) {
        super(id, 8f);
        canBeCopied = false;
        canBeUsedByNPC = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 2));
    }

    @Override
    protected float getSpiritualityCost() {
        return 500;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide)
            return;

        if (!(level instanceof ServerLevel serverLevel))
            return;

        Vec3 spawnPos = entity.position();
        String pathway = BeyonderData.getPathway(entity);
        int sequence = BeyonderData.getSequence(entity);

        // Spawn the clone
        AvatarEntity clone = new AvatarEntity(
                ModEntities.ERROR_AVATAR.get(),
                serverLevel,
                entity.getUUID(),
                pathway,
                sequence
        );
        clone.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        // Clone should not move or attack
        clone.setNoAi(true);

        serverLevel.addFreshEntity(clone);

        ParticleUtil.spawnSphereParticles(serverLevel, purpleDust, spawnPos, 5, 80);
        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.ENCHANT,
                spawnPos.add(0, 1, 0), 40, 0.4, 1.0, 0.4, 0.05);

        serverLevel.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.5f, 1.2f);

        // Turn invisible for 5s
        entity.addEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY, 20 * 5, 0, false, false, false));

        // Redirect any mobs targeting
        AbilityUtil.getNearbyEntities(entity, serverLevel, spawnPos, 20).forEach(e -> {
            if (e instanceof Mob mob
                    && mob.getTarget() != null
                    && mob.getTarget().getUUID().equals(entity.getUUID())) {
                mob.setTarget(clone);
            }
        });

        // Erase clone after 5s
        ServerScheduler.scheduleDelayed(20 * 5, () -> {
            if (clone.isAlive()) {
                ParticleUtil.spawnSphereParticles(serverLevel, purpleDust,
                        clone.position(), 4, 60);
                ParticleUtil.spawnParticles(serverLevel, ParticleTypes.ENCHANT,
                        clone.position().add(0, 1, 0), 30, 0.4, 1.0, 0.4, 0.05);
                serverLevel.playSound(null,
                        clone.getX(), clone.getY(), clone.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT,
                        entity.getSoundSource(), 1.0f, 0.8f);
                clone.discard();
            }
        }, serverLevel);
    }
}