package de.jakob.lotm.item.custom;

import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ScrollGustVortexItem extends Item {

    private static final int PULL_RADIUS = 15;
    private static final int DURATION_TICKS = 20 * 5;
    private static final double PULL_SCALE = 0.015;
    private static final int COOLDOWN_TICKS = 20 * 20;

    // 3 rings: bottom = narrow, top = widest
    private static final int FUNNEL_RINGS = 3;
    private static final double FUNNEL_MAX_RADIUS = 1.5;  // widest at top
    private static final double FUNNEL_MIN_RADIUS = 0.3;  // narrowest at bottom
    private static final double FUNNEL_HEIGHT = 1.5;

    public ScrollGustVortexItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        Vec3 targetLoc = AbilityUtil.getTargetLocation(player, 27, 2);

        level.playSound(null, targetLoc.x, targetLoc.y, targetLoc.z,
                SoundEvents.ENDER_DRAGON_FLAP, SoundSource.BLOCKS, 2.0f, 0.8f);

        final int[] angleTick = {0};

        ServerScheduler.scheduleForDuration(0, 1, DURATION_TICKS, () -> {
            angleTick[0]++;
            double angleOffset = angleTick[0] * 25.0 * (Math.PI / 180.0);

            for (int ring = 0; ring < FUNNEL_RINGS; ring++) {
                // t=0 → bottom (narrow), t=1 → top (wide)
                double t = (double) ring / (FUNNEL_RINGS - 1);
                double ringY = targetLoc.y + FUNNEL_HEIGHT * t;
                double ringRadius = FUNNEL_MIN_RADIUS + (FUNNEL_MAX_RADIUS - FUNNEL_MIN_RADIUS) * t;
                int particlesInRing = Math.max(4, (int) (ringRadius * 8));

                for (int p = 0; p < particlesInRing; p++) {
                    double angle = angleOffset + (2 * Math.PI * p / particlesInRing);
                    double px = targetLoc.x + Math.cos(angle) * ringRadius;
                    double pz = targetLoc.z + Math.sin(angle) * ringRadius;
                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            px, ringY, pz,
                            1, 0, 0.01, 0, 0);
                }
            }

            // Wind sound every second while active
            if (angleTick[0] % 20 == 0) {
                level.playSound(null, targetLoc.x, targetLoc.y, targetLoc.z,
                        SoundEvents.ELYTRA_FLYING, SoundSource.BLOCKS, 0.6f,
                        0.5f + level.random.nextFloat() * 0.3f);
            }

            // Pull every 2 ticks
            if (angleTick[0] % 2 == 0) {
                AbilityUtil.getAllNearbyEntities(player, serverLevel, targetLoc, PULL_RADIUS)
                        .forEach(e -> {
                            e.setDeltaMovement(
                                    targetLoc.subtract(e.position()).scale(PULL_SCALE));
                            BlockPos nextPos = BlockPos.containing(
                                    e.position().add(targetLoc.subtract(e.position()).scale(.4)));
                            if (!serverLevel.getBlockState(nextPos)
                                    .getCollisionShape(serverLevel, nextPos).isEmpty()) {
                                e.teleportTo(e.getX(), e.getY() + 1, e.getZ());
                            }
                            e.hurtMarked = true;
                        });
            }
        }, serverLevel);

        if (!player.getAbilities().instabuild)
            player.getItemInHand(usedHand).shrink(1);

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}