package de.jakob.lotm.item.custom;

import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Random;

public class ScrollBlazingExplosionItem extends Item {

    private static final double DAMAGE = DamageLookup.lookupDamage(6, .45);
    private static final int COOLDOWN_TICKS = 20 * 10;

    private static final DustParticleOptions dust = new DustParticleOptions(
            new Vector3f(1.0f, .95f, .95f), 2.0f);

    public ScrollBlazingExplosionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        Vec3 targetPos = AbilityUtil.getTargetLocation(player, 20, 1.4f);

        level.explode(player, targetPos.x, targetPos.y,     targetPos.z, 4, false, Level.ExplosionInteraction.NONE);
        level.explode(player, targetPos.x, targetPos.y + 1, targetPos.z, 4, false, Level.ExplosionInteraction.NONE);

        level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 3.0f, 1.0f);

        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.FLAME,
                targetPos, 800, 1.5, 4, 1.5, .02);
        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.SMOKE,
                targetPos, 200, 1.5, 4, 1.5, .02);
        ParticleUtil.spawnParticles(serverLevel, ParticleTypes.EXPLOSION,
                targetPos, 60, 1.5, 4, 1.5, .02);
        ParticleUtil.spawnParticles(serverLevel, dust,
                targetPos, 250, 1.5, 4, 1.5, 0);

        AbilityUtil.damageNearbyEntities(serverLevel, player, 6,
                DAMAGE, targetPos, true, false);

        Random r = new Random();
        for (int i = 0; i < 7; i++) {
            FallingBlockEntity falling = FallingBlockEntity.fall(
                    level,
                    BlockPos.containing(targetPos.x, targetPos.y, targetPos.z)
                            .offset(r.nextInt(-1, 1), 2, r.nextInt(-1, 1)),
                    i % 2 == 0
                            ? Blocks.MAGMA_BLOCK.defaultBlockState()
                            : Blocks.BASALT.defaultBlockState()
            );

            Vec3 motion = new Vec3(
                    r.nextDouble(-3, 3),
                    r.nextDouble(3.5, 5),
                    r.nextDouble(-3, 3)
            ).normalize().scale(1.2);
            falling.setDeltaMovement(motion);
            falling.disableDrop(); // scroll doesn't grief

            ServerScheduler.scheduleForDuration(0, 1, 40, () -> {
                falling.setDeltaMovement(
                        falling.getDeltaMovement().x,
                        falling.getDeltaMovement().y - 0.03,
                        falling.getDeltaMovement().z);
                falling.hurtMarked = true;
            });
        }

        if (!player.getAbilities().instabuild)
            player.getItemInHand(usedHand).shrink(1);

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}