package de.jakob.lotm.abilities.hermit.passives;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityHandler;
import de.jakob.lotm.abilities.PassiveAbilityItem;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class CombatPryingAbility extends PassiveAbilityItem {

    public CombatPryingAbility(Properties properties) {
        super(properties);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 8));
    }

    @Override
    public void tick(Level level, LivingEntity entity) {}


    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 30_000L; // 30 seconds

    // S8-5 → tier 0, S4-3 → tier 1, S2-1 → tier 2
    private static int getTier(int sequence) {
        if (sequence <= 2) return 2;
        if (sequence <= 4) return 1;
        return 0;
    }

    // Tier → effect amplifier (level 1/2/3 = amplifier 0/1/2)
    private static int getAmplifier(int tier) {
        return tier;
    }

    // Fire resistance: 5s/10s/15s. Everything else is 10s.
    private static int getDuration(int tier, boolean isFireResistance) {
        if (isFireResistance)
            return 20 * (5 + tier * 5);
        return 20 * 10;
    }


    private record BuffDef(net.minecraft.core.Holder<MobEffect> effect, boolean isFireResistance) {}

    private static BuffDef getBuffForPathway(String pathway) {
        return switch (pathway) {
            case "fool", "error", "door" ->
                    new BuffDef(MobEffects.MOVEMENT_SPEED, false);
            case "sun", "tyrant", "white_tower", "hanged_man", "visionary" ->
                    new BuffDef(MobEffects.DAMAGE_BOOST, false);
            case "darkness", "death", "twilight_giant" ->
                    new BuffDef(MobEffects.DAMAGE_RESISTANCE, false);
            case "demoness", "red_priest" ->
                    new BuffDef(MobEffects.FIRE_RESISTANCE, true);
            case "hermit", "paragon" ->
                    new BuffDef(MobEffects.MOVEMENT_SPEED, false);
            case "wheel_of_fortune" ->
                    new BuffDef(ModEffects.LUCK, false);
            case "abyss", "chained" ->
                    new BuffDef(MobEffects.DAMAGE_RESISTANCE, false);
            case "black_emperor", "justiciar" ->
                    new BuffDef(MobEffects.DAMAGE_BOOST, false);
            case "mother", "moon" ->
                    new BuffDef(MobEffects.HEALTH_BOOST, false);
            default -> null;
        };
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (!BeyonderData.isBeyonder(player))
            return;
        if (!BeyonderData.getPathway(player).equals("hermit"))
            return;

        int sequence = BeyonderData.getSequence(player);
        if (sequence > 8)
            return;

        // Check passive is active for this player
        boolean hasPassive = PassiveAbilityHandler.ITEMS.getEntries().stream()
                .anyMatch(e -> e.get() instanceof CombatPryingAbility ability
                        && ability.shouldApplyTo(player));
        if (!hasPassive)
            return;

        // Check cooldown
        UUID uuid = player.getUUID();
        long now = System.currentTimeMillis();
        Long expiresAt = cooldowns.get(uuid);
        if (expiresAt != null && now < expiresAt)
            return;

        // Identify attacker pathway
        DamageSource source = event.getSource();
        if (source.getEntity() == null)
            return;
        if (!(source.getEntity() instanceof LivingEntity attacker))
            return;
        if (!BeyonderData.isBeyonder(attacker))
            return;

        String attackerPathway = BeyonderData.getPathway(attacker);
        BuffDef buff = getBuffForPathway(attackerPathway);
        if (buff == null)
            return;

        // Apply buff
        int tier = getTier(sequence);
        int duration = getDuration(tier, buff.isFireResistance());
        int amplifier = buff.isFireResistance() ? 0 : getAmplifier(tier);

        player.addEffect(new MobEffectInstance(
                buff.effect(),
                duration,
                amplifier,
                false, true, true
        ));

        // Start cooldown
        cooldowns.put(uuid, now + COOLDOWN_MS);
    }
}