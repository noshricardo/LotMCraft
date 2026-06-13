package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.item.ModItems;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.ParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class CreateScrollAbility extends SelectableAbility {
    public CreateScrollAbility(String id) {
        super(id, 30);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 6));
    }

    @Override
    public float getSpiritualityCost() {
        return 150;
    }

    @Override
    public String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.create_scroll.gustvortex",
                "ability.lotmcraft.create_scroll.lightrealm",
                "ability.lotmcraft.create_scroll.blazingexplosion",
                "ability.lotmcraft.create_scroll.rainstorm",
                "ability.lotmcraft.create_scroll.clarity"};
    }

    @Override
    public void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if(level.isClientSide)
            return;

        switch (abilityIndex) {
            case 0 -> gustvortex((ServerLevel) level, entity);
            case 1 -> lightrealm((ServerLevel) level, entity);
            case 2 -> blazingexplosion((ServerLevel) level, entity);
            case 3 -> rainstorm((ServerLevel) level, entity);
            case 4 -> clarity((ServerLevel) level, entity);

        }
    }

    private void clarity(ServerLevel level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return;

        boolean isHighEnoughSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 4;

        if (!isHighEnoughSequence) {
            if (!(entity instanceof ServerPlayer serverPlayer))
                return;

            boolean hasGlowstone = serverPlayer.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.EMERALD));

            if (!hasGlowstone) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires emerald to create!").withColor(0xFFff124d)
                ));
                return;
            }

            serverPlayer.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.EMERALD))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        ParticleUtil.spawnParticles(level, ParticleTypes.ENCHANT,
                player.getEyePosition().subtract(0, 0.3, 0),
                20, 0.3, 0.3, 0.3, 0.05);

        player.addItem(new ItemStack(ModItems.SCROLL_CLARITY.get()));
    }

    private void rainstorm(ServerLevel level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return;

        boolean isHighEnoughSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 4;

        if (!isHighEnoughSequence) {
            if (!(entity instanceof ServerPlayer serverPlayer))
                return;

            boolean hasGlowstone = serverPlayer.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.LAPIS_LAZULI));

            if (!hasGlowstone) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires lapis lazuli to create!").withColor(0xFFff124d)
                ));
                return;
            }

            serverPlayer.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.LAPIS_LAZULI))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        ParticleUtil.spawnParticles(level, ParticleTypes.ENCHANT,
                player.getEyePosition().subtract(0, 0.3, 0),
                20, 0.3, 0.3, 0.3, 0.05);

        player.addItem(new ItemStack(ModItems.SCROLL_RAINSTORM.get()));
    }

    private void blazingexplosion(ServerLevel level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return;

        boolean isHighEnoughSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 4;

        if (!isHighEnoughSequence) {
            if (!(entity instanceof ServerPlayer serverPlayer))
                return;

            boolean hasGlowstone = serverPlayer.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.REDSTONE));

            if (!hasGlowstone) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires redstone dust to create!").withColor(0xFFff124d)
                ));
                return;
            }

            serverPlayer.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.REDSTONE))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        ParticleUtil.spawnParticles(level, ParticleTypes.ENCHANT,
                player.getEyePosition().subtract(0, 0.3, 0),
                20, 0.3, 0.3, 0.3, 0.05);

        player.addItem(new ItemStack(ModItems.SCROLL_BLAZINGEXPLOSION.get()));
    }

    private void lightrealm(ServerLevel level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return;

        boolean isHighEnoughSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 4;

        if (!isHighEnoughSequence) {
            if (!(entity instanceof ServerPlayer serverPlayer))
                return;

            boolean hasGlowstone = serverPlayer.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.GLOWSTONE_DUST));

            if (!hasGlowstone) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires glowstone dust to create!").withColor(0xFFff124d)
                ));
                return;
            }

            serverPlayer.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.GLOWSTONE_DUST))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        ParticleUtil.spawnParticles(level, ParticleTypes.ENCHANT,
                player.getEyePosition().subtract(0, 0.3, 0),
                20, 0.3, 0.3, 0.3, 0.05);

        player.addItem(new ItemStack(ModItems.SCROLL_LIGHTREALM.get()));
    }

    private void gustvortex(ServerLevel level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return;

        boolean isHighEnoughSequence = BeyonderData.isBeyonder(entity)
                && BeyonderData.getSequence(entity) <= 4;

        if (!isHighEnoughSequence) {
            if (!(entity instanceof ServerPlayer serverPlayer))
                return;

            boolean hasGlowstone = serverPlayer.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(net.minecraft.world.item.Items.IRON_INGOT));

            if (!hasGlowstone) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Requires iron ingot to create!").withColor(0xFFff124d)
                ));
                return;
            }

            serverPlayer.getInventory().items.stream()
                    .filter(stack -> stack.is(net.minecraft.world.item.Items.IRON_INGOT))
                    .findFirst()
                    .ifPresent(stack -> stack.shrink(1));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        ParticleUtil.spawnParticles(level, ParticleTypes.ENCHANT,
                player.getEyePosition().subtract(0, 0.3, 0),
                20, 0.3, 0.3, 0.3, 0.05);

        // Give the scroll to the player
        player.addItem(new ItemStack(ModItems.SCROLL_GUSTVORTEX.get()));
    }
}
