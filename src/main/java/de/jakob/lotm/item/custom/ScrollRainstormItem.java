package de.jakob.lotm.item.custom;

import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
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
import org.joml.Vector3f;

public class ScrollRainstormItem extends Item {
    // sequence 6 strength (its fixed cuz idk how to make it scale as an item)
    private static final double DAMAGE_PER_HIT = DamageLookup.lookupDps(6, .775, 10, 20);
    private static final int DURATION_TICKS = 20 * 10;
    private static final int COOLDOWN_TICKS = 20 * 15;

    private static final DustParticleOptions dustOptions = new DustParticleOptions(
            new Vector3f(30 / 255f, 120 / 255f, 255 / 255f), 1.5f
    );

    public ScrollRainstormItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // Target location — where the player is looking, up to 15 blocks away
        Vec3 startPos = AbilityUtil.getTargetLocation(player, 15, 2);
        Vec3 cloudPos = startPos.add(0, 8, 0);
        Vec3 rainPos  = startPos.add(0, 3, 0);

        // Visual + sound loop — every 4 ticks for the duration
        ServerScheduler.scheduleForDuration(0, 4, DURATION_TICKS, () -> {
            level.playSound(null, rainPos.x, rainPos.y, rainPos.z,
                    SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 2, 1);
            ParticleUtil.spawnParticles(serverLevel, ParticleTypes.CLOUD,
                    cloudPos, 120, 5, .4, 5, 0);
            ParticleUtil.spawnParticles(serverLevel, ParticleTypes.RAIN,
                    rainPos, 180, 4, 4, 4, 0);
            ParticleUtil.spawnParticles(serverLevel, dustOptions,
                    rainPos, 35, 4, 4, 4, 0);
            ParticleUtil.spawnParticles(serverLevel, ParticleTypes.SNEEZE,
                    rainPos, 45, 4, 4, 4, 0);
        }, serverLevel);

        ServerScheduler.scheduleForDuration(0, 10, DURATION_TICKS, () ->
                        AbilityUtil.damageNearbyEntities(serverLevel, player, 5,
                                DAMAGE_PER_HIT, startPos, true, false, true, 0),
                serverLevel);

        if (!player.getAbilities().instabuild) {
            player.getItemInHand(usedHand).shrink(1);
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}