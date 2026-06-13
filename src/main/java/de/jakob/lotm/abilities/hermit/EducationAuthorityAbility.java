package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;


public class EducationAuthorityAbility extends Ability {
    public EducationAuthorityAbility(String id) {
        super(id, 3f);
        canBeCopied = false;
        canBeUsedByNPC = true;
        this.cannotBeStolen = true;
    }
    private final DustParticleOptions dust = new DustParticleOptions(
            new Vector3f(250 / 255f, 201 / 255f, 102 / 255f), 1.5f);

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 1));
    }

    @Override
    public float getSpiritualityCost() {return 1500;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide)
            return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 20, 2);
        if (target == null)
            return;

        ServerLevel serverLevel = (ServerLevel) level;

        Vec3 headPos = target.getEyePosition().subtract(0, 0.3, 0);
        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.ENCHANT,
                headPos, 200, 0.6, 0.6, 0.6, 0.15);
        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.END_ROD,
                headPos, 60, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE,
                target.getSoundSource(), 2.0f, 0.5f);

        ServerScheduler.scheduleDelayed(3, () ->
                        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.ENCHANT,
                                headPos, 120, 0.8, 0.4, 0.8, 0.2),
                serverLevel);

        int targetSequence = BeyonderData.getSequence(target);

        if (BeyonderData.isBeyonder(target) && targetSequence >= 3) {
            // S3 and above will be stuned
            int casterSequence = BeyonderData.getSequence(entity);
            int durationTicks = switch (casterSequence) {
                case 0 -> 20 * 9;
                case 1 -> 20 * 7;
                default -> 20 * 5;
            };

            target.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS, durationTicks, 0, false, false, true));
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN, durationTicks, 10, false, false, true));

            if (target instanceof Mob mob) {
                mob.setNoAi(true);
                ServerScheduler.scheduleDelayed(durationTicks, () -> mob.setNoAi(false));
            }

            Location loc = new Location(target.position(), serverLevel);
            ServerScheduler.scheduleForDuration(0, 1, durationTicks, () -> {
                target.setDeltaMovement(Vec3.ZERO);
                target.hurtMarked = true;

                loc.setPosition(target.position());
                loc.setLevel(target.level());

                double angle = (System.currentTimeMillis() / 50.0) % (2 * Math.PI);
                double headY = target.getY() + target.getBbHeight() + 0.3;
                double r = 0.7;

                for (int i = 0; i < 3; i++) {
                    double a = angle + (2 * Math.PI * i / 3);
                    serverLevel.sendParticles(ParticleTypes.ENCHANT,
                            target.getX() + Math.cos(a) * r,
                            headY,
                            target.getZ() + Math.sin(a) * r,
                            1, 0, 0.05, 0, 0);
                }
            }, serverLevel);

        } else {
            // Below S3 (or non-beyonder): frenzy-style effect scaled to S1
            int amplifier;
            if (AbilityUtil.isTargetSignificantlyWeaker(entity, target)) {
                amplifier = 6;
            } else if (AbilityUtil.isTargetSignificantlyStronger(entity, target)) {
                amplifier = 1;
            } else if (BeyonderData.isBeyonder(entity) && BeyonderData.isBeyonder(target)) {
                int casterSeq = BeyonderData.getSequence(entity);
                amplifier = (targetSequence <= casterSeq) ? 2 : random.nextInt(3, 5);
            } else {
                amplifier = 1;
            }

            if (!target.hasEffect(ModEffects.LOOSING_CONTROL) || target.getEffect(ModEffects.LOOSING_CONTROL).getAmplifier() < amplifier)
                target.addEffect(new MobEffectInstance(ModEffects.LOOSING_CONTROL, 20 * 10, amplifier));

            target.hurt(entity.damageSources().source(ModDamageTypes.LOOSING_CONTROL),
                    (float) (DamageLookup.lookupDamage(1, 0.85) * multiplier(entity)));

            target.getData(ModAttachments.SANITY_COMPONENT)
                    .increaseSanityAndSync((float) (-0.065f * multiplier(entity) * multiplier(entity)), target);

            ParticleUtil.spawnParticles(serverLevel, dust, target.getEyePosition(), 80, 0.5f); }
    }
}