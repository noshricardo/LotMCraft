package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.item.ModItems;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class StellarGuidanceAbility extends SelectableAbility {
    private final HashSet<UUID> isFlying = new HashSet<>();
    private static final HashSet<UUID> boundEntities = new HashSet<>();
    private static final HashMap<UUID, Integer> figurineNumbers = new HashMap<>();
    private static final DustParticleOptions starDust = new DustParticleOptions(
            new Vector3f(224 / 255f, 86 / 255f, 237 / 255f), 2f
    );

    public StellarGuidanceAbility(String id) {
        super(id, 2);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 5));
    }

    @Override
    public float getSpiritualityCost() {
        return 100;
    }

    final int radius = 16;

    DustParticleOptions pillardustOptions = new DustParticleOptions(
            new Vector3f(204 / 255f, 118 / 255f, 212 / 255f), 2f
    );

    @Override
    public String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.stellar_guidance.starpillar",
                "ability.lotmcraft.stellar_guidance.stellarself",
                "ability.lotmcraft.stellar_guidance.starshuttle",
                "ability.lotmcraft.stellar_guidance.stellarbind",
        };
    }

    @Override
    public void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if (level.isClientSide)
            return;

        switch (abilityIndex) {
            case 0 -> starpillar((ServerLevel) level, entity);
            case 1 -> stellarself((ServerLevel) level, entity);
            case 2 -> starshuttle((ServerLevel) level, entity);
            case 3 -> stellarbind((ServerLevel) level, entity);
        }
    }

    private void stellarself(ServerLevel level, LivingEntity entity) {
        if (figurineNumbers.containsKey(entity.getUUID())
                && figurineNumbers.get(entity.getUUID()) >= 5)
            return;

        if (!figurineNumbers.containsKey(entity.getUUID()))
            figurineNumbers.put(entity.getUUID(), 1);
        else
            figurineNumbers.replace(entity.getUUID(),
                    figurineNumbers.get(entity.getUUID()) + 1);

        if (entity instanceof Player player) {
            player.addItem(new ItemStack(ModItems.STAR_SUBSTITUTE.get()));
        }

        ParticleUtil.spawnParticles(level, starDust,
                entity.getEyePosition().subtract(0, 0.4, 0),
                30, 0.4, 0.6, 0.4, 0);
        ParticleUtil.spawnParticles(level, ParticleTypes.END_ROD,
                entity.getEyePosition().subtract(0, 0.4, 0),
                15, 0.4, 0.6, 0.4, 0.05);

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, entity.getSoundSource(), 1.0f, 1.8f);
    }

    @SubscribeEvent
    public static void takeDamage(LivingDamageEvent.Pre event) {
        if (!figurineNumbers.containsKey(event.getEntity().getUUID()))
            return;

        if (event.getSource().is(ModDamageTypes.LOOSING_CONTROL))
            return;

        int num = figurineNumbers.get(event.getEntity().getUUID());
        if (num <= 0)
            return;

        figurineNumbers.put(event.getEntity().getUUID(), num - 1);
        event.setNewDamage(0);

        LivingEntity entity = event.getEntity();
        Vec3 pos = entity.position();
        Level level = entity.level();

        ParticleUtil.spawnParticles((ServerLevel) level, starDust,
                entity.getEyePosition().subtract(0, 0.4, 0),
                35, 0.3, 0.8, 0.3, 0);
        ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.END_ROD,
                entity.getEyePosition().subtract(0, 0.4, 0),
                20, 0.3, 0.8, 0.3, 0.05);

        Random r = new Random();
        Vec3 newPos = pos.add(r.nextDouble(-7, 7), r.nextDouble(-1, 3), r.nextDouble(-7, 7));

        for (int i = 0; i < 65; i++) {
            if (level.getBlockState(BlockPos.containing(newPos.x, newPos.y, newPos.z)).isAir())
                break;
            newPos = pos.add(r.nextDouble(-7, 7), r.nextDouble(-1, 3), r.nextDouble(-7, 7));
        }

        entity.teleportTo(newPos.x, newPos.y, newPos.z);

        if (entity instanceof Player player) {
            int index = player.getInventory().findSlotMatchingItem(
                    new ItemStack(ModItems.STAR_SUBSTITUTE.get()));
            if (index != -1)
                player.getInventory().removeItem(index, 1);
        }

        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ARMOR_STAND_HIT, SoundSource.BLOCKS, 3, 1);
        level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.6f, 1);
    }


    private void starpillar(ServerLevel level, LivingEntity entity) {
        Vec3 initialPos = AbilityUtil.getTargetLocation(entity, radius, .75f).add(0, 14, 0);

        List<BlockPos> lights = new ArrayList<>();
        AtomicReference<Vec3> currentPos = new AtomicReference<>(initialPos);

        level.playSound(null, initialPos.x, initialPos.y - 14, initialPos.z,
                SoundEvents.AMETHYST_BLOCK_CHIME, entity.getSoundSource(), 3.0f, 1.0f);

        ServerScheduler.scheduleForDuration(0, 1, 18, () -> {
                    Vec3 pos = currentPos.get();
                    ParticleUtil.spawnCircleParticles((ServerLevel) level, ParticleTypes.FIREWORK, pos, 1.4, 20);
                    ParticleUtil.spawnCircleParticles((ServerLevel) level, pillardustOptions, pos, 1.4, 20);

                    BlockPos blockPos = BlockPos.containing(pos);
                    if (level.getBlockState(blockPos).isAir()) {
                        level.setBlockAndUpdate(blockPos, Blocks.LIGHT.defaultBlockState());
                        lights.add(blockPos);
                    }

                    AbilityUtil.damageNearbyEntities((ServerLevel) level, entity, 2.5f,
                            DamageLookup.lookupDamage(8, .8) * multiplier(entity),
                            pos, true, false, false, 10,
                            ModDamageTypes.source(level, ModDamageTypes.PURIFICATION, entity));

                    currentPos.set(pos.subtract(0, 1, 0));
                }, null, (ServerLevel) level,
                () -> AbilityUtil.getTimeInArea(entity, new Location(entity.position(), level)));

        ServerScheduler.scheduleDelayed(18, () ->
                        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(
                                (ServerLevel) level, initialPos.subtract(0, 14, 0),
                                entity, this, interactionFlags, interactionRadius, interactionCacheTicks)),
                (ServerLevel) level,
                () -> AbilityUtil.getTimeInArea(entity, new Location(entity.position(), level)));

        ServerScheduler.scheduleDelayed(22, () ->
                        lights.forEach(l -> level.setBlockAndUpdate(l, Blocks.AIR.defaultBlockState())),
                (ServerLevel) level,
                () -> AbilityUtil.getTimeInArea(entity, new Location(entity.position(), level)));
    }


    private void stellarbind(ServerLevel level, LivingEntity entity) {
        if (level.isClientSide)
            return;

        LivingEntity targetEntity = AbilityUtil.getTargetEntity(entity, 16, 2);
        if (targetEntity == null)
            return;

        if (boundEntities.contains(targetEntity.getUUID())) {
            if (entity instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Entity is already bound by your stars.")
                                .withColor(0xFFff124d)));
            }
            return;
        }

        int duration = 20 * 10;

        level.playSound(null, entity,
                Blocks.AMETHYST_BLOCK.getSoundType(
                        Blocks.AMETHYST_BLOCK.defaultBlockState(), level,
                        BlockPos.containing(entity.position()), null).getBreakSound(),
                SoundSource.BLOCKS, 5, 1);

        boundEntities.add(targetEntity.getUUID());

        if (!BeyonderData.isBeyonder(targetEntity)
                || BeyonderData.getSequence(targetEntity) - 1 > BeyonderData.getSequence(entity)) {
            if (targetEntity instanceof Mob mob) {
                mob.setNoAi(true);
                ServerScheduler.scheduleDelayed(duration, () -> mob.setNoAi(false));
            }
        }

        Location loc = new Location(targetEntity.position(), targetEntity.level());

        // Particle orbit loop — runs every tick for the bind duration,
        // tracking the target as they move
        AtomicReference<UUID> taskIdRef = new AtomicReference<>();
        AtomicInteger particleTick = new AtomicInteger(0);

        UUID taskId = ServerScheduler.scheduleForDuration(0, 1, duration, () -> {
            // Blink escape check
            if (InteractionHandler.isInteractionPossibleForEntity(
                    loc, "blink_escape", BeyonderData.getSequence(entity), targetEntity)) {
                ServerScheduler.cancel(taskIdRef.get());

                targetEntity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                targetEntity.removeEffect(MobEffects.WEAKNESS);
                targetEntity.removeEffect(MobEffects.BLINDNESS);
                if (targetEntity instanceof Mob mob) mob.setNoAi(false);

                boundEntities.remove(targetEntity.getUUID());
                return;
            }

            // Apply binding effects every 5 ticks
            if (particleTick.get() % 5 == 0) {
                targetEntity.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, 10, 10, false, false, false));
                targetEntity.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS, 7, 10, false, false, false));
                targetEntity.addEffect(new MobEffectInstance(
                        MobEffects.BLINDNESS, 3, 10, false, false, false));
                targetEntity.setDeltaMovement(Vec3.ZERO);
                targetEntity.hurtMarked = true;
            }

            // Floating firework particles orbiting above/around the target
            double angle = (particleTick.get() * 18.0) * (Math.PI / 180.0); // orbit in 20/1s ticks
            double headY = targetEntity.getY() + targetEntity.getBbHeight() + 0.3;

            double outerRadius = 1.1;
            double ox = targetEntity.getX() + Math.cos(angle + Math.PI / 2) * outerRadius;
            double oz = targetEntity.getZ() + Math.sin(angle + Math.PI / 2) * outerRadius;
            level.sendParticles(ParticleTypes.FIREWORK, ox, headY + 0.5, oz, 1, 0, 0.05, 0, 0);

            loc.setLevel(targetEntity.level());
            loc.setPosition(targetEntity.position());
            particleTick.incrementAndGet();
        });

        taskIdRef.set(taskId);
        ServerScheduler.scheduleDelayed(duration, () -> boundEntities.remove(targetEntity.getUUID()));
    }


    private void starshuttle(ServerLevel level, LivingEntity entity) {
        if (level.isClientSide)
            return;

        if (!(entity instanceof Player player))
            return;

        if (isFlying.contains(player.getUUID())) {
            isFlying.remove(player.getUUID());
            return;
        }

        isFlying.add(player.getUUID());

        Location supplier = new Location(entity.position(), entity.level());

        List<AtomicBoolean> canceled = ParticleUtil.createParticleSpirals(
                ParticleTypes.FIREWORK, supplier, .25, 1.35, entity.getEyeHeight(),
                .75, 3, 20 * 60 * 12, 8, 2);

        AtomicBoolean shouldStop = new AtomicBoolean(false);

        ServerScheduler.scheduleForDuration(0, 1, 20 * 60 * 12, () -> {
            if (shouldStop.get())
                return;

            if (!isFlying.contains(player.getUUID())) {
                canceled.forEach(b -> b.set(true));
                shouldStop.set(true);
                return;
            }

            if (BeyonderData.getSpirituality(player) < 3) {
                isFlying.remove(player.getUUID());
                return;
            }

            BeyonderData.reduceSpirituality(player, 3);

            if (player.isShiftKeyDown())
                player.setDeltaMovement(Vec3.ZERO);
            else
                player.setDeltaMovement(player.getLookAngle().normalize().multiply(.4, .4, .4));

            player.resetFallDistance();
            player.hurtMarked = true;

            if (!player.level().isClientSide) {
                supplier.setPosition(player.position());
                supplier.setLevel(player.level());
            }
        }, (ServerLevel) level);
    }
}