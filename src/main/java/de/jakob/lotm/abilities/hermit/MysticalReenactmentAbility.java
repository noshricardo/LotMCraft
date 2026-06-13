package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.common.AngelAuthorityAbility;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.demoness.InvisibilityAbility;
import de.jakob.lotm.attachments.MemorisedEntities;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.dimension.ModDimensions;
import de.jakob.lotm.dimension.SpiritWorldHandler;
import de.jakob.lotm.entity.ModEntities;
import de.jakob.lotm.entity.custom.ability_entities.darkness_pathway.ConcealedDomainEntity;
import de.jakob.lotm.entity.custom.ability_entities.hermit_pathway.AvalonEntity;
import de.jakob.lotm.entity.custom.ability_entities.projectiles.LonginusSpearProjectileEntity;
import de.jakob.lotm.events.AdvancementsEventHandler;
import de.jakob.lotm.item.ModItems;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.OpenShapeShiftingScreenPacket;
import de.jakob.lotm.network.packets.toClient.SyncUnlockedMythsPacket;
import de.jakob.lotm.network.packets.toServer.AbilitySelectionPacket;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.VectorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.UUID;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MysticalReenactmentAbility extends SelectableAbility {
    public static final HashSet<UUID> invisiblePlayers = new HashSet<>();

    // Index 0 (derive) is always unlocked — no advancement needed.
    private static final Map<Integer, String> ADVANCEMENT_REQUIREMENTS = Map.of(
            1, "myth_longinus",
            2, "myth_hades",
            3, "myth_cinderella",
            4, "myth_avalon",
            5, "myth_ariadne"
    );

    // Client-side cache of which indices this player has unlocked (populated via packet)
    private final Set<Integer> clientUnlockedIndices = new HashSet<>(Set.of(0));

    public void setClientUnlockedIndices(List<Integer> indices) {
        clientUnlockedIndices.clear();
        clientUnlockedIndices.add(0); // derive always unlocked
        clientUnlockedIndices.addAll(indices);
    }

    // Myth items consumed by derive to unlock each sub-ability. Lazy to avoid registry issues.
    private static Map<String, Item> getMythItems() {
        return Map.of(
                "myth_longinus",   ModItems.MYTH_LONGINUS.get(),
                "myth_hades",      ModItems.MYTH_HADES.get(),
                "myth_cinderella", ModItems.MYTH_CINDERELLA.get(),
                "myth_avalon",     ModItems.MYTH_AVALON.get(),
                "myth_ariadne",    ModItems.MYTH_ARIADNE.get()
        );
    }

    public MysticalReenactmentAbility(String id) {
        super(id, 10f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 4));
    }

    @Override
    protected float getSpiritualityCost() {
        return 750;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.mystical_reenactment.derive",
                "ability.lotmcraft.mystical_reenactment.longinus",
                "ability.lotmcraft.mystical_reenactment.hades",
                "ability.lotmcraft.mystical_reenactment.cinderella",
                "ability.lotmcraft.mystical_reenactment.avalon",
                "ability.lotmcraft.mystical_reenactment.ariadne"
        };
    }

    private boolean isUnlockedServer(ServerPlayer player, int index) {
        if (index == 0) return true;
        String advancement = ADVANCEMENT_REQUIREMENTS.get(index);
        if (advancement == null) return false;
        return AdvancementsEventHandler.isAdvancementDone(player, advancement);
    }

    private boolean isUnlockedClient(int index) {
        return clientUnlockedIndices.contains(index);
    }

    public void syncUnlockedToClient(ServerPlayer player) {
        List<Integer> unlocked = ADVANCEMENT_REQUIREMENTS.entrySet().stream()
                .filter(e -> AdvancementsEventHandler.isAdvancementDone(player, e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        PacketHandler.sendToPlayer(player, new SyncUnlockedMythsPacket(unlocked));
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (!(entity instanceof Player)) {
            castSelectedAbility(level, entity, random.nextInt(getAbilityNames().length));
            return;
        }

        if (!selectedAbilities.containsKey(entity.getUUID())) {
            selectedAbilities.put(entity.getUUID(), 0);
        }

        int selected = selectedAbilities.get(entity.getUUID());

        // Server-side: fall back to derive if locked
        if (entity instanceof ServerPlayer serverPlayer && !isUnlockedServer(serverPlayer, selected)) {
            selected = 0;
            selectedAbilities.put(entity.getUUID(), 0);
        }

        castSelectedAbility(level, entity, selected);
    }

    @Override
    public void nextAbility(LivingEntity entity) {
        if (getAbilityNames().length == 0) return;

        if (!selectedAbilities.containsKey(entity.getUUID())) {
            selectedAbilities.put(entity.getUUID(), 0);
        }

        int current = selectedAbilities.get(entity.getUUID());
        int next = current;

        for (int i = 1; i < getAbilityNames().length; i++) {
            int candidate = (current + i) % getAbilityNames().length;
            if (isUnlockedClient(candidate)) {
                next = candidate;
                break;
            }
        }

        selectedAbilities.put(entity.getUUID(), next);
        PacketHandler.sendToServer(new AbilitySelectionPacket(getId(), next));
    }

    @Override
    public void previousAbility(LivingEntity entity) {
        if (getAbilityNames().length == 0) return;

        if (!selectedAbilities.containsKey(entity.getUUID())) {
            selectedAbilities.put(entity.getUUID(), 0);
        }

        int current = selectedAbilities.get(entity.getUUID());
        int prev = current;

        for (int i = 1; i < getAbilityNames().length; i++) {
            int candidate = ((current - i) % getAbilityNames().length + getAbilityNames().length) % getAbilityNames().length;
            if (isUnlockedClient(candidate)) {
                prev = candidate;
                break;
            }
        }

        selectedAbilities.put(entity.getUUID(), prev);
        PacketHandler.sendToServer(new AbilitySelectionPacket(getId(), prev));
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch (abilityIndex) {
            case 0 -> derive(level, entity);
            case 1 -> longinus(level, entity);
            case 2 -> hades(level, entity);
            case 3 -> cinderella(level, entity);
            case 4 -> avalon(level, entity);
            case 5 -> ariadne(level, entity);
        }
    }

    private void derive(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        Map<String, Item> mythItems = getMythItems();

        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;

            String advancementPath = null;
            for (Map.Entry<String, Item> entry : mythItems.entrySet()) {
                if (entry.getValue() == stack.getItem()) {
                    advancementPath = entry.getKey();
                    break;
                }
            }

            if (advancementPath == null) continue;

            if (!AdvancementsEventHandler.isAdvancementDone(player, advancementPath)) {
                stack.shrink(1);
                AdvancementsEventHandler.grantAdvancement(player, advancementPath);
                // Sync updated unlocked indices to client immediately
                syncUnlockedToClient(player);
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal("Myth derived!")
                ));
                return;
            }
        }

        player.connection.send(new ClientboundSetActionBarTextPacket(
                Component.literal("No myth to derive.")
        ));
    }

    private void ariadne(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        ServerLevel targetLevel;
        Vec3 targetPos;

        if (!player.level().dimension().equals(ModDimensions.SPIRIT_WORLD_DIMENSION_KEY)) {
            ResourceKey spiritWorld = ResourceKey.create((ResourceKey) Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "spirit_world"));
            targetLevel = player.getServer().getLevel(spiritWorld);
            targetPos = SpiritWorldHandler.getCoordinatesInSpiritWorld(player.position(), targetLevel);
            BlockPos pos = BlockPos.containing(targetPos);

            while (!targetLevel.getBlockState(pos).isAir()) {
                pos = pos.above();
            }
            BlockPos below = pos.below();
            if (targetLevel.getBlockState(below).isAir()) {
                targetLevel.setBlockAndUpdate(below, Blocks.END_STONE.defaultBlockState());
            }
            player.teleportTo(targetLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        } else {
            targetLevel = player.server.getLevel(Level.OVERWORLD);
            if (targetLevel == null) return;

            targetPos = SpiritWorldHandler.getCoordinatesInOverworld(player.position(), targetLevel);
            BlockPos pos = BlockPos.containing(targetPos);

            while (!targetLevel.getBlockState(pos).isAir()) {
                pos = pos.above();
            }
            BlockPos below = pos.below();
            if (targetLevel.getBlockState(below).isAir()) {
                targetLevel.setBlockAndUpdate(below, Blocks.STONE.defaultBlockState());
            }
            player.teleportTo(targetLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        }

        ParticleUtil.spawnParticles((ServerLevel) player.level(), ParticleTypes.END_ROD,
                player.position(), 200, 2.0, 0.001);
        targetLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private void avalon(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        AvalonEntity avalon = new AvalonEntity(ModEntities.AVALON.get(), serverLevel);
        avalon.setOwner(entity);
        avalon.setPos(entity.position());
        serverLevel.addFreshEntity(avalon);
        AvalonEntity.registerForOwner(entity.getUUID(), avalon);
    }

    private void cinderella(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        MemorisedEntities memorisedEntities = player.getData(ModAttachments.MEMORISED_ENTITIES);
        PacketDistributor.sendToPlayer(
                player,
                new OpenShapeShiftingScreenPacket(memorisedEntities.getMemorisedEntityTypes())
        );
    }

    private void hades(Level level, LivingEntity entity) {
        if(!level.isClientSide) {

            // make invisible
            invisiblePlayers.add(entity.getUUID());
            entity.setInvisible(true);
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 15, 20, false, false, false));

            //make visible again
            AtomicReference<UUID> taskIdRef = new AtomicReference<>();
            UUID taskId = ServerScheduler.scheduleForDuration(0, 10, 20 * 10, () -> {
                if(InteractionHandler.isInteractionPossible(new Location(entity.position(), entity.level()), "light_strong", BeyonderData.getSequence(entity))) {
                    entity.setInvisible(false);
                    entity.removeEffect(MobEffects.INVISIBILITY);
                    ServerScheduler.cancel(taskIdRef.get());
                }
            }, () -> invisiblePlayers.remove(entity.getUUID()), (ServerLevel) level, () -> AbilityUtil.getTimeInArea(entity, new Location(entity.position(), level)));
            taskIdRef.set(taskId);
        }
    }


        private void longinus(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        Vec3 startPos = VectorUtil.getRelativePosition(
                entity.getEyePosition().add(entity.getLookAngle().normalize()),
                entity.getLookAngle().normalize(),
                0, random.nextDouble(3.5f, 6f), random.nextDouble(-.1, .6));
        Vec3 direction = AbilityUtil.getTargetLocation(entity, 50, 1.4f).subtract(startPos).normalize();

        level.playSound(null, startPos.x, startPos.y, startPos.z,
                SoundEvents.BEACON_ACTIVATE, entity.getSoundSource(), 1.0f, 1.0f);

        LonginusSpearProjectileEntity spear = new LonginusSpearProjectileEntity(
                level, entity, DamageLookup.lookupDamage(4, .8) * multiplier(entity),
                BeyonderData.isGriefingEnabled(entity), null);
        spear.setPos(startPos.x, startPos.y, startPos.z);
        spear.shoot(direction.x, direction.y, direction.z, 3f, 0);
        level.addFreshEntity(spear);
    }
}