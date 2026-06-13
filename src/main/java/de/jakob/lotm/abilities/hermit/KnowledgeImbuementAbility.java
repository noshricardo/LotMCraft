package de.jakob.lotm.abilities.hermit;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.item.ModItems;
import de.jakob.lotm.potions.PotionRecipeItem;
import de.jakob.lotm.potions.PotionRecipeItemHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeImbuementAbility extends Ability {

    private static Map<String, Item> getPathwayCards() {
        return Map.of(
                "fool", ModItems.FOOL_Card.get()
                // Add more pathways here as cards are registered
        );
    }

    public KnowledgeImbuementAbility(String id) {
        super(id, 0);
        this.canBeCopied = false;
        this.canBeUsedByNPC = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("hermit", 1));
    }

    @Override
    public float getSpiritualityCost() {
        return 0;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        // Collect all recipe items in inventory and group by pathway
        Map<String, Map<Integer, ItemStack>> recipesByPathway = new HashMap<>();

        for (ItemStack stack : player.getInventory().items) {
            if (!(stack.getItem() instanceof PotionRecipeItem recipeItem)) continue;
            if (recipeItem.getRecipe() == null) continue;

            String pathway = recipeItem.getRecipe().potion().getPathway();
            int sequence = recipeItem.getRecipe().potion().getSequence();

            recipesByPathway
                    .computeIfAbsent(pathway, k -> new HashMap<>())
                    .put(sequence, stack);
        }

        // Try each pathway the player has at least one recipe for
        boolean anySuccess = false;

        for (Map.Entry<String, Map<Integer, ItemStack>> entry : recipesByPathway.entrySet()) {
            String pathway = entry.getKey();
            Map<Integer, ItemStack> ownedSequences = entry.getValue();

            // Check if card exists for this pathway
            if (!getPathwayCards().containsKey(pathway)) continue;

            // Need all 9 sequences (can add 0 once added)
            List<PotionRecipeItem> allRequired = PotionRecipeItemHandler.selectAllOfPathway(pathway)
                    .stream()
                    .filter(r -> r.getRecipe().potion().getSequence() >= 1
                            && r.getRecipe().potion().getSequence() <= 9)
                    .toList();

            boolean missingAny = false;
            for (PotionRecipeItem required : allRequired) {
                int seq = required.getRecipe().potion().getSequence();
                if (!ownedSequences.containsKey(seq)) {
                    missingAny = true;
                    break;
                }
            }

            if (missingAny) {
                player.displayClientMessage(
                        Component.literal("Missing recipes!"),
                        true
                );
                continue;
            }

            // Consume all 9 recipes
            for (PotionRecipeItem required : allRequired) {
                int seq = required.getRecipe().potion().getSequence();
                ItemStack stack = ownedSequences.get(seq);
                stack.shrink(1);
            }

            // Give the card
            ItemStack card = new ItemStack(getPathwayCards().get(pathway));
            if (!player.getInventory().add(card)) {
                player.drop(card, false);
            }

            anySuccess = true;
        }

        if (!anySuccess && recipesByPathway.isEmpty()) {
            player.displayClientMessage(
                    Component.literal("Missing recipes!"),
                    true
            );
        }
    }
}