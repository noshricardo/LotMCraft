package de.jakob.lotm.abilities.wheel_of_fortune;

import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.item.ModItems;
import de.jakob.lotm.item.custom.PaperCraneItem;
import de.jakob.lotm.item.custom.SpecialCoinItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class FatedConnectionAbility extends SelectableAbility {

    public FatedConnectionAbility(String id) {
        super(id, 5);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("wheel_of_fortune", 1));
    }

    @Override
    public float getSpiritualityCost() {
        return 1;
    }

    @Override
    public String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.fated_connection.crane",
                "ability.lotmcraft.fated_connection.coin"
        };
    }

    @Override
    public void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if (!(entity instanceof Player)) abilityIndex = 0;
        switch (abilityIndex) {
            case 0 -> crane(level, entity);
            case 1 -> coin(level, entity);
        }
    }

    // ==================== CRANE ====================
    // Requires 1 paper in inventory

    private void crane(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        if (!consumeFromInventory(player, new ItemStack(Items.PAPER))) {
            sendFailure(player, "You need a piece of paper.");
            return;
        }

        ItemStack crane = PaperCraneItem.create(player);
        player.addItem(crane);
    }

    // ==================== COIN ====================
    // Requires 1 gold nugget in inventory

    private void coin(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        if (!consumeFromInventory(player, new ItemStack(Items.GOLD_NUGGET))) {
            sendFailure(player, "You need a gold nugget.");
            return;
        }

        ItemStack coin = SpecialCoinItem.create(player);
        player.addItem(coin);
    }

    // ==================== HELPERS ====================

    /**
     * Searches the player's inventory for the given item and removes one.
     * Returns true if found and consumed, false otherwise.
     */
    private boolean consumeFromInventory(ServerPlayer player, ItemStack required) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (!stack.is(required.getItem())) continue;
            player.getInventory().removeItem(i, 1);
            return true;
        }
        return false;
    }

    private void sendFailure(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message)
                .withStyle(net.minecraft.ChatFormatting.RED));
    }
}