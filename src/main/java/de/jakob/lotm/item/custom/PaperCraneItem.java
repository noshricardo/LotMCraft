package de.jakob.lotm.item.custom;

import de.jakob.lotm.events.HonorificNamesEventHandler;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.beyonderMap.PendingPrayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class PaperCraneItem extends Item {
    public static final String KEY_CREATOR = "CreatorUUID";
    public static final String KEY_CREATOR_NAME = "CreatorName";

    public PaperCraneItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create(ServerPlayer creator) {
        ItemStack stack = new ItemStack(de.jakob.lotm.item.ModItems.PAPER_CRANE.get());
        CompoundTag tag = new CompoundTag();
        tag.putUUID(KEY_CREATOR, creator.getUUID());
        tag.putString(KEY_CREATOR_NAME, creator.getName().getString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_NAME,
                Component.literal("Paper Crane")
                        .withStyle(ChatFormatting.WHITE));
        return stack;
    }

    public static UUID getCreatorUUID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return tag.hasUUID(KEY_CREATOR) ? tag.getUUID(KEY_CREATOR) : null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(stack);
        if (!(player instanceof ServerPlayer user)) return InteractionResultHolder.pass(stack);

        UUID creatorUUID = getCreatorUUID(stack);
        if (creatorUUID == null) return InteractionResultHolder.fail(stack);

        // Don't let creator pray to themselves
        if (creatorUUID.equals(user.getUUID())) {
            user.sendSystemMessage(Component.literal("This crane is yours.").withStyle(ChatFormatting.GRAY));
            return InteractionResultHolder.fail(stack);
        }

        ServerPlayer creator = user.serverLevel().getServer().getPlayerList().getPlayer(creatorUUID);
        if (creator == null) {
            user.sendSystemMessage(Component.literal("The creator is not available.").withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(stack);
        }

        triggerPrayer(user, creator);

        // Consume the item
        stack.shrink(1);
        return InteractionResultHolder.consume(stack);
    }

    private static void triggerPrayer(ServerPlayer sender, ServerPlayer target) {
        PendingPrayer prayer = new PendingPrayer(
                sender.getUUID(),
                sender.getName().getString(),
                BeyonderData.getPathway(sender),
                BeyonderData.getSequence(sender),
                sender.getX(), sender.getY(), sender.getZ()
        );

        HonorificNamesEventHandler.addPendingPrayer(target.getUUID(), prayer);
        HonorificNamesEventHandler.answerState.add(
                new com.mojang.datafixers.util.Pair<>(target.getUUID(), sender.getUUID()));

        target.sendSystemMessage(
                Component.empty()
                        .append(Component.literal("Someone used your connected crane... ")
                                .withStyle(ChatFormatting.WHITE))
                        .append(HonorificNamesEventHandler.formNotification(sender))
        );
    }
}