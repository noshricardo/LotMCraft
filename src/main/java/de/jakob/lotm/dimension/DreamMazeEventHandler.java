package de.jakob.lotm.dimension;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.attachments.DreamMazeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.*;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class DreamMazeEventHandler {

    private static final Map<UUID, Integer> entryTicks = new HashMap<>();
    private static final int EJECT_TICKS = 20 * 30;

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (serverLevel.dimension().equals(ModDimensions.DREAM_MAZE_DIMENSION_KEY)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ModDimensions.DREAM_MAZE_DIMENSION_KEY)) {
            entryTicks.remove(entity.getUUID());
            return;
        }

        // Give night vision to everyone inside the dimension
        entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 0, false, false, false));

        if (!(entity instanceof ServerPlayer player)) return;

        DreamMazeData data = DreamMazeData.get(serverLevel.getServer());
        if (!data.isOccupant(player.getUUID())) return;

        UUID casterUUID = data.getCasterForOccupant(player.getUUID());
        if (casterUUID == null) return;

        // user has no time limit
        if (casterUUID.equals(player.getUUID())) return;

        int ticks = entryTicks.getOrDefault(player.getUUID(), 0) + 1;
        entryTicks.put(player.getUUID(), ticks);

        if (ticks >= EJECT_TICKS) {
            entryTicks.remove(player.getUUID());
            ejectPlayer(player, serverLevel.getServer(), data);
        }
    }

    public static void ejectPlayer(ServerPlayer player, MinecraftServer server, DreamMazeData data) {
        double[] pos = data.getReturnPosition(player.getUUID());
        String dimStr = data.getReturnDimension(player.getUUID());

        if (pos == null || dimStr == null) {
            data.removeOccupant(player.getUUID());
            return;
        }

        ResourceKey<Level> returnDim = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                net.minecraft.resources.ResourceLocation.parse(dimStr));
        ServerLevel returnLevel = server.getLevel(returnDim);

        if (returnLevel == null) returnLevel = server.overworld();

        player.teleportTo(returnLevel, pos[0], pos[1], pos[2],
                player.getYRot(), player.getXRot());

        data.removeOccupant(player.getUUID());
        entryTicks.remove(player.getUUID());
        data.setDirty();
    }

    public static void resetTimer(UUID uuid) {
        entryTicks.remove(uuid);
    }
}