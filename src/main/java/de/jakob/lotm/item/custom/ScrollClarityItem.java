package de.jakob.lotm.item.custom;

import de.jakob.lotm.particle.ModParticles;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.RingEffectManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScrollClarityItem extends Item {

    // Fixed heal amount — weaker than the mother ability since no multiplier
    private static final float HEAL_AMOUNT = 8.0f;
    private static final int COOLDOWN_TICKS = 20 *7;

    public ScrollClarityItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // Heal
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + HEAL_AMOUNT));

        // Cleanse all harmful effects
        player.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
                .toList()
                .forEach(player::removeEffect);

        // Extinguish fire
        player.setRemainingFireTicks(0);

        // Restore hunger and saturation
        player.getFoodData().setSaturation(20);
        player.getFoodData().setFoodLevel(20);

        // Visuals
        RingEffectManager.createRingForAll(
                player.getEyePosition().subtract(0, .4, 0),
                2, 60,
                122 / 255f, 235 / 255f, 124 / 255f, 1,
                .5f, .75f,
                serverLevel);

        ParticleUtil.spawnParticles(serverLevel, ModParticles.HEALING.get(),
                player.getEyePosition().subtract(0, .3, 0),
                35, .9);

        level.playSound(null,
                player.position().x, player.position().y, player.position().z,
                SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1, 1);

        if (!player.getAbilities().instabuild)
            player.getItemInHand(usedHand).shrink(1);

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}