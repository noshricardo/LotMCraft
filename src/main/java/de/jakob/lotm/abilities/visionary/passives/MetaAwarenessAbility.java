package de.jakob.lotm.abilities.visionary.passives;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityItem;
import de.jakob.lotm.events.HonorificNamesEventHandler;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.beyonderMap.PendingPrayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class MetaAwarenessAbility extends PassiveAbilityItem {
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_MS = 5000; // 5s cooldown per trigger so they cant be spammed up

    public MetaAwarenessAbility(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 1));
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        String message = event.getRawText();
        ServerPlayer sender = event.getPlayer();

        if (!(sender.level() instanceof ServerLevel serverLevel)) return;

        // Check all online players who have this passive
        for (ServerPlayer candidate : serverLevel.getServer().getPlayerList().getPlayers()) {
            // Don't trigger if the sender is saying their own name
            if (candidate.getUUID().equals(sender.getUUID())) continue;

            // Check if this player has the MetaAwareness passive
            if (!hasMetaAwareness(candidate)) continue;

            String username = candidate.getName().getString();

            // Full username must appear in the message (case-insensitive)
            if (!message.toLowerCase().contains(username.toLowerCase())) continue;

            triggerAutoPrayer(sender, candidate);
        }
    }


    // Called from PlayerDivinationSelectedPacket.handle when a divination succeeds. If the divined target has MetaAwareness, auto-pray  to the diviner.
    public static void onDivined(ServerPlayer diviner, ServerPlayer target) {
        if (!hasMetaAwareness(target)) return;
        triggerAutoPrayer(diviner, target);
    }


    private static void triggerAutoPrayer(ServerPlayer sender, ServerPlayer target) {
        // Cooldown check on the target (prevent spam if their name is said repeatedly)
        long now = System.currentTimeMillis();
        Long lastTrigger = COOLDOWNS.get(target.getUUID());
        if (lastTrigger != null && now - lastTrigger < COOLDOWN_MS) return;
        COOLDOWNS.put(target.getUUID(), now);

        // Store pending prayer so target can respond via Honorific Names menu
        PendingPrayer prayer = new PendingPrayer(
                sender.getUUID(),
                sender.getName().getString(),
                BeyonderData.getPathway(sender),
                BeyonderData.getSequence(sender),
                sender.getX(), sender.getY(), sender.getZ()
        );

        HonorificNamesEventHandler.addPendingPrayer(target.getUUID(), prayer);

        // Add to pending prayers via HonorificNamesEventHandler
        HonorificNamesEventHandler.answerState.add(
                new com.mojang.datafixers.util.Pair<>(target.getUUID(), sender.getUUID()));

        // Use reflection-free approach: directly notify target
        target.sendSystemMessage(
                Component.empty()
                        .append(Component.literal("[Meta Awareness] ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE))
                        .append(HonorificNamesEventHandler.formNotification(sender))
        );
    }

    private static boolean hasMetaAwareness(ServerPlayer player) {
        // Check if the player has this passive item in their inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() instanceof MetaAwarenessAbility) {
                return true;
            }
        }
        return false;
    }
}