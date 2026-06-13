package de.jakob.lotm.abilities.hermit;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.particle.ModParticles;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.VectorUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WarlockSpellsAbility extends SelectableAbility {
    private final Set<UUID> isCastingWind = new HashSet<>();
    private static final HashSet<UUID> boundEntities = new HashSet<>();
    private final DustParticleOptions dust = new DustParticleOptions(new Vector3f(33 / 255f, 163 / 255f, 52 / 255f), .7f);
    private final DustParticleOptions dust2 = new DustParticleOptions(
            new Vector3f(30 / 255f, 120 / 255f, 255 / 255f),
            1.5f
    );



    public WarlockSpellsAbility(String id) {
        super(id, 1f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 7));
    }

    @Override
    protected float getSpiritualityCost() {
        return 20;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.warlock_spells.flame",
                "ability.lotmcraft.warlock_spells.purify",
                "ability.lotmcraft.warlock_spells.wind",
                "ability.lotmcraft.warlock_spells.wave",
                "ability.lotmcraft.warlock_spells.snare"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch(abilityIndex) {
            case 0 -> flame(level, entity);
            case 1 -> purify(level, entity);
            case 2 -> wind(level, entity);
            case 3 -> wave(level, entity);
            case 4 -> snare(level, entity);

        }
    }

    private void purify(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;


        // Check item requirement — skipped for S5 +
        boolean checkSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 5;

        if (!checkSequence && entity instanceof ServerPlayer player) {
            boolean hasSunflower = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.SUNFLOWER));

            if (!hasSunflower) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires a sunflower to cast!").withColor(0xFFff124d)
                ));
                return;
            }

            player.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.SUNFLOWER))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        Vec3 startPos = entity.getEyePosition().subtract(0, .2, 0).add(entity.getLookAngle().normalize());
        level.playSound(null, startPos.x, startPos.y, startPos.z, SoundEvents.BLAZE_SHOOT, entity.getSoundSource(), 2.0f, .5f);

        ParticleUtil.drawParticleLine(
                (ServerLevel) level,
                ParticleTypes.END_ROD,
                startPos,
                entity.getLookAngle().normalize(),
                2,
                .5, 8, .4
        );

        ParticleUtil.drawParticleLine(
                (ServerLevel) level,
                ModParticles.HOLY_FLAME.get(),
                startPos,
                entity.getLookAngle().normalize(),
                2,
                .5, 32, .4
        );

        ParticleUtil.drawParticleLine(
                (ServerLevel) level,
                ParticleTypes.FIREWORK,
                startPos,
                entity.getLookAngle().normalize(),
                2,
                .5, 12, .4
        );

        AbilityUtil.damageNearbyEntities((ServerLevel) level, entity, 2.75, DamageLookup.lookupDamage(7, .9) * multiplier(entity), startPos, true, false, true, 0, ModDamageTypes.source(level, ModDamageTypes.PURIFICATION, entity));

        BlockState block = level.getBlockState(BlockPos.containing(startPos));
        if(block.isAir()) {
            level.setBlockAndUpdate(BlockPos.containing(startPos), Blocks.LIGHT.defaultBlockState());
        }

        ServerScheduler.scheduleDelayed(25, () -> level.setBlockAndUpdate(BlockPos.containing(startPos), Blocks.AIR.defaultBlockState()));
    }

    private void wave(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;

        // Check item requirement — skipped for S5+
        boolean checkSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 5;

        if (!checkSequence && entity instanceof ServerPlayer player) {
            boolean hasKelp = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.KELP));

            if (!hasKelp) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires kelp to cast!").withColor(0xFFff124d)
                ));
                return;
            }

            player.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.KELP))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        Vec3 startPos = entity.getEyePosition().add(0, .5, 0);

        level.playSound(null, startPos.x, startPos.y, startPos.z, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, entity.getSoundSource(), 1.0f, 1.0f);

        ServerScheduler.scheduleDelayed(18, () -> AbilityUtil.damageNearbyEntities((ServerLevel) level, entity, 5.5, DamageLookup.lookupDamage(7, .875) * multiplier(entity), entity.position().add(0, .2, 0), true, false, true, 0));

        AtomicDouble i = new AtomicDouble(0.6);
        ServerScheduler.scheduleForDuration(0, 1, 24, () -> {
            double ySubtraction = 2 * ((1/((10 * i.get()) - 9)) - 1);
            Vec3 currentPos = startPos.add(0, ySubtraction, 0);
            double radius = i.get() < .71 ? i.get() : i.get() * 2;
            ParticleUtil.spawnCircleParticles((ServerLevel) level, dust2, currentPos, radius, (int) (radius * 16));
            ParticleUtil.spawnCircleParticles((ServerLevel) level, ParticleTypes.FALLING_WATER, currentPos, radius, (int) (radius * 16));
            i.set(i.get() + .1);
        }, (ServerLevel) level);
    }

    private void flame(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;

        // Check item requirement — skipped for S5 +
        boolean checkSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 5;

        if (!checkSequence && entity instanceof ServerPlayer player) {
            boolean hasCoal = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.COAL));

            if (!hasCoal) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires coal to cast!").withColor(0xFFff124d)
                ));
                return;
            }

            player.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.COAL))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        Vec3 startPos = VectorUtil.getRelativePosition(entity.getEyePosition().add(entity.getLookAngle().normalize()), entity.getLookAngle().normalize(), 0, random.nextDouble(-.65, .65), random.nextDouble(-.1, .6));
        Vec3 direction = AbilityUtil.getTargetLocation(entity, 10, 1.4f).subtract(startPos).normalize();

        AtomicReference<Vec3> currentPos = new AtomicReference<>(startPos);

        AtomicBoolean hasHit = new AtomicBoolean(false);

        level.playSound(null, startPos.x, startPos.y, startPos.z, SoundEvents.BLAZE_SHOOT, entity.getSoundSource(), 1.0f, 1.0f);

        ServerScheduler.scheduleForDuration(0, 1, 20 * 20, () -> {
            if(hasHit.get())
                return;

            Vec3 pos = currentPos.get();

            if(AbilityUtil.damageNearbyEntities((ServerLevel) level, entity, 2.5f, DamageLookup.lookupDamage(7, .83) * (float) multiplier(entity), pos, true, false, true, 0, 20 * 5)) {
                hasHit.set(true);
                return;
            }

            if(!level.getBlockState(BlockPos.containing(pos.x, pos.y, pos.z)).isAir()) {
                if(BeyonderData.isGriefingEnabled(entity)) {
                    pos = pos.subtract(direction);
                    level.setBlockAndUpdate(BlockPos.containing(pos.x, pos.y, pos.z), Blocks.FIRE.defaultBlockState());
                }
                hasHit.set(true);
                return;
            }

            ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.FLAME, pos, 20, 0.27, 0.02);
            ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.SMOKE, pos, 12, 0.27, 0.02);

            currentPos.set(pos.add(direction));
        }, null, (ServerLevel) level, () -> AbilityUtil.getTimeInArea(entity, new Location(currentPos.get(), level)));
    }

    private void snare(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;

        // Check item requirement — skipped for S5 and above
        boolean checkSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 5;

        if (!checkSequence && entity instanceof ServerPlayer player) {
            boolean hasSeeds = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.WHEAT_SEEDS));

            if (!hasSeeds) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires wheat seeds to cast!").withColor(0xFFff124d)
                ));
                return;
            }

            player.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.WHEAT_SEEDS))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        LivingEntity targetEntity = AbilityUtil.getTargetEntity(entity, 16, 2);
        if(targetEntity == null)
            return;

        if(boundEntities.contains(targetEntity.getUUID())) {
            if(entity instanceof ServerPlayer player) {
                ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(Component.literal("Entity is already bound by your plants.").withColor(0xFFff124d));
                player.connection.send(packet);
            }
            return;
        }

        int duration = 20 * 5;

        for(int i = 0; i < 12; i++) {
            Vec3 targetLoc = targetEntity.position().add(0, .4, 0);

            double x = random.nextBoolean() ? random.nextDouble(-4.5, -2) : random.nextDouble(2, 4.5);
            double z = random.nextBoolean() ? random.nextDouble(-4.5, -2) : random.nextDouble(2, 4.5);

            Vec3 startPos = targetLoc.add(x, -.5, z);

            animateParticleLine(new Location(startPos, level), targetLoc, 3, 0, duration);
        }

        level.playSound(null, entity, Blocks.GRASS_BLOCK.getSoundType(Blocks.GRASS_BLOCK.defaultBlockState(), level, BlockPos.containing(entity.position().x, entity.position().y, entity.position().z), null).getBreakSound(), SoundSource.BLOCKS, 5,1);


        boundEntities.add(targetEntity.getUUID());

        if(!BeyonderData.isBeyonder(targetEntity) || BeyonderData.getSequence(targetEntity) - 1 > BeyonderData.getSequence(entity)) {
            if(targetEntity instanceof Mob) {
                ((Mob) targetEntity).setNoAi(true);
                ServerScheduler.scheduleDelayed(duration, () -> ((Mob) targetEntity).setNoAi(false));
            }
        }

        Location loc = new Location(targetEntity.position(), targetEntity.level());

        AtomicReference<UUID> taskIdRef = new AtomicReference<>();
        UUID taskId = ServerScheduler.scheduleForDuration(0, 5, duration, () -> {
            // Blink Escape - only the bound entity can free itself
            if(InteractionHandler.isInteractionPossibleForEntity(loc, "blink_escape", BeyonderData.getSequence(entity), targetEntity)) {
                ServerScheduler.cancel(taskIdRef.get());

                targetEntity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (targetEntity instanceof Mob mob) mob.setNoAi(false);

                boundEntities.remove(targetEntity.getUUID());
                return;
            }

            targetEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 10, false, false, false));
            targetEntity.setDeltaMovement(new Vec3(0, 0, 0));
            targetEntity.hurtMarked = true;

            loc.setLevel(targetEntity.level());
            loc.setPosition(targetEntity.position());
        });
        taskIdRef.set(taskId);


        ServerScheduler.scheduleDelayed(duration, () -> boundEntities.remove(targetEntity.getUUID()));
    }

    private final Random random = new Random();

    private void wind(Level level, LivingEntity entity) {
        if(level.isClientSide)
            return;

        // Check item requirement — skipped for S5+
        boolean checkSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 5;

        if (!checkSequence && entity instanceof ServerPlayer player) {
            boolean hasFeather = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.FEATHER));

            if (!hasFeather) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires a feather to cast!").withColor(0xFFff124d)
                ));
                return;
            }

            player.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.FEATHER))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }


        if(isCastingWind.contains(entity.getUUID()))
            return;

        isCastingWind.add(entity.getUUID());

        ServerScheduler.scheduleForDuration(0, 1, 20 * 6, () -> {
            Vec3 dir = entity.getLookAngle().normalize().scale(.5);
            AbilityUtil.getNearbyEntities(entity, (ServerLevel) level, entity.position(), 10).forEach(e -> {
                e.setDeltaMovement(dir);
                e.hurtMarked = true;
            });

            if(random.nextBoolean())
                level.playSound(null, entity.position().x, entity.position().y, entity.position().z, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.BLOCKS, .8f, 1);

            for(int i = 0; i < 10; i++) {
                Vec3 pos = VectorUtil.getRelativePosition(entity.getEyePosition(), dir, random.nextDouble(-2, 2.5), random.nextDouble(-7, 7), random.nextDouble(-3, 3));

                ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.CLOUD, pos, 0, dir.x, dir.y, dir.z, 1);
            }

        }, () -> isCastingWind.remove(entity.getUUID()), (ServerLevel) level, () -> AbilityUtil.getTimeInArea(entity, new de.jakob.lotm.util.data.Location(entity.position(), level)));
    }

    private void animateParticleLine(Location startLoc, Vec3 end, int step, int interval, int duration) {
        if(!(startLoc.getLevel() instanceof ServerLevel level))
            return;
        AtomicInteger tick = new AtomicInteger(0);

        float distance = (float) end.distanceTo(startLoc.getPosition());
        float bezierSteps = .15f / distance;

        List<Vec3> points = VectorUtil.createBezierCurve(startLoc.getPosition(), end, bezierSteps, 2.5f, 1);

        ServerScheduler.scheduleForDuration(0, interval, duration, () -> {
            for(int i = 0; i < Math.min(tick.get(), points.size() - step); i+=step) {
                for(int j = 0; j < step; j++) {
                    boolean shouldSpawn = tick.get() < duration || random.nextInt(3) == 0;
                    if(shouldSpawn)
                        ParticleUtil.spawnParticles(level, dust, points.get(i + j), 0, 0, 0);
                }
            }

            tick.addAndGet(1);
        });
    }
}
