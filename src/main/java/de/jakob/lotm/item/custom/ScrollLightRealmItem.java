package de.jakob.lotm.item.custom;

import de.jakob.lotm.damage.ModDamageTypes;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
public class ScrollLightRealmItem extends Item {

    // Weaker than the real domain — S6 scale, smaller radius
    private static final double DAMAGE_PER_HIT = DamageLookup.lookupDps(6, .2, 10, 20);
    private static final int RADIUS = 5; // half of unshadowed's 40
    private static final int DURATION_TICKS = 20 * 5; // half of unshadowed's 30s
    private static final int COOLDOWN_TICKS = 20 * 20;

    private static final DustParticleOptions dust = new DustParticleOptions(
            new Vector3f(1f, 185 / 255f, 3 / 255f), 6f); // smaller dust than original

    public ScrollLightRealmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        Vec3 startPos = player.position();

        // Fill nearby air blocks adjacent to solid blocks with light
        List<BlockPos> blocks = AbilityUtil.getBlocksInSphereRadius(
                        serverLevel, startPos, RADIUS, true, false, false)
                .stream()
                .filter(b -> {
                    BlockState state = level.getBlockState(b);
                    return state.isAir() && (
                            !level.getBlockState(b.below()).isAir() ||
                                    !level.getBlockState(b.above()).isAir() ||
                                    !level.getBlockState(b.north()).isAir() ||
                                    !level.getBlockState(b.south()).isAir() ||
                                    !level.getBlockState(b.east()).isAir() ||
                                    !level.getBlockState(b.west()).isAir());
                })
                .toList();

        blocks.forEach(b -> level.setBlockAndUpdate(b, Blocks.LIGHT.defaultBlockState()));

        level.playSound(null, startPos.x, startPos.y, startPos.z,
                SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 2.0f, 1.0f);

        // Damage + glow loop every 10 ticks
        ServerScheduler.scheduleForDuration(0, 10, DURATION_TICKS, () -> {
            ParticleUtil.spawnParticles(serverLevel, dust,
                    startPos, 60, 12, 4, 12, 0);
            ParticleUtil.spawnParticles(serverLevel, ParticleTypes.END_ROD,
                    startPos, 60, 12, 4, 12, 0);

            AbilityUtil.addPotionEffectToNearbyEntities(serverLevel, player, RADIUS, startPos,
                    new MobEffectInstance(MobEffects.GLOWING, 20 * 2, 1, false, false, false));

            AbilityUtil.getNearbyEntities(player, serverLevel, startPos, RADIUS)
                    .stream()
                    .filter(e -> e instanceof Mob || e instanceof Player)
                    .forEach(e -> e.hurt(
                            ModDamageTypes.source(level, ModDamageTypes.PURIFICATION, player),
                            (float) DAMAGE_PER_HIT));

        }, () -> blocks.forEach(b -> {
            if (level.getBlockState(b).is(Blocks.LIGHT))
                level.setBlockAndUpdate(b, Blocks.AIR.defaultBlockState());
        }), serverLevel);

        if (!player.getAbilities().instabuild)
            player.getItemInHand(usedHand).shrink(1);

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}