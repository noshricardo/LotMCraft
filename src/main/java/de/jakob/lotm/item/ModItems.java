package de.jakob.lotm.item;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.artifacts.SealedArtifactItem;
import de.jakob.lotm.block.ModBlocks;
import de.jakob.lotm.item.custom.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LOTMCraft.MOD_ID);

    public static final DeferredItem<Item> FOOL_Card = ITEMS.registerItem("fool_card", Item::new, new Item.Properties());
    public static final DeferredItem<Item> CRYSTAL_BALL = ITEMS.registerItem("crystal_ball", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> CANE = ITEMS.registerItem("cane", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MOD_ICON = ITEMS.registerItem("lotm_icon", Item::new, new Item.Properties());
    public static final DeferredItem<Item> PAPER_FIGURINE_SUBSTITUTE = ITEMS.registerItem("paper_figurine_substitute", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MIRROR = ITEMS.registerItem("mirror", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> BLOOD = ITEMS.registerItem("blood", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAR_SUBSTITUTE = ITEMS.registerItem("star_substitute", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STORY_BOOK = ITEMS.registerItem("story_book", StoryBookItem::new, new Item.Properties().stacksTo(1));

    // Myth items — consumed by Mystical Reenactment's derive ability to unlock sub-abilities
    public static final DeferredItem<Item> MYTH_LONGINUS = ITEMS.registerItem("myth_longinus", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MYTH_HADES = ITEMS.registerItem("myth_hades", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MYTH_CINDERELLA = ITEMS.registerItem("myth_cinderella", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MYTH_AVALON = ITEMS.registerItem("myth_avalon", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MYTH_ARIADNE = ITEMS.registerItem("myth_ariadne", Item::new, new Item.Properties().stacksTo(1));

    public static final Supplier<Item> SPECIAL_COIN = ITEMS.register("special_coin",
            () -> new SpecialCoinItem(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> PAPER_CRANE = ITEMS.register("paper_crane",
            () -> new PaperCraneItem(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> MARIONETTE_CONTROLLER = ITEMS.register("marionette_controller",
            () -> new MarionetteControllerItem(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> SUBORDINATE_CONTROLLER = ITEMS.register("subordinate_controller",
            () -> new SubordinateControllerItem(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> EXCAVATED_AREA_ITEM = ITEMS.register("excavated_area",
            () -> new ExcavatedAreaItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> SUN_ITEM = ITEMS.register("sun",
            () -> new SunItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> MOON_ITEM = ITEMS.register("moon",
            () -> new MoonItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> GUIDING_BOOK = ITEMS.register("guiding_book",
            () -> new GuidingBookItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> SCROLL_GUSTVORTEX = ITEMS.register("scroll_gustvortex",
            () -> new ScrollGustVortexItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> SCROLL_LIGHTREALM = ITEMS.register("scroll_lightrealm",
            () -> new ScrollLightRealmItem(new Item.Properties()
                    .stacksTo(1)
            )
    );

    public static final Supplier<Item> SCROLL_BLAZINGEXPLOSION = ITEMS.register("scroll_blazingexplosion",
            () -> new ScrollBlazingExplosionItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> SCROLL_RAINSTORM = ITEMS.register("scroll_rainstorm",
            () -> new ScrollRainstormItem(new Item.Properties()
                    .stacksTo(1)
            )
    );
    public static final Supplier<Item> SCROLL_CLARITY = ITEMS.register("scroll_clarity",
            () -> new ScrollClarityItem(new Item.Properties()
                    .stacksTo(1)
            )
    );

    public static final DeferredHolder<Item, Item> SEALED_ARTIFACT = ITEMS.register("sealed_artifact",
            () -> new SealedArtifactItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
    public static final DeferredHolder<Item, Item> SEALED_ARTIFACT_BELL = ITEMS.register("sealed_artifact_bell",
            () -> new SealedArtifactItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
    public static final DeferredHolder<Item, Item> SEALED_ARTIFACT_STAR = ITEMS.register("sealed_artifact_star",
            () -> new SealedArtifactItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
    public static final DeferredHolder<Item, Item> SEALED_ARTIFACT_CHAIN = ITEMS.register("sealed_artifact_chain",
            () -> new SealedArtifactItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
    public static final DeferredHolder<Item, Item> SEALED_ARTIFACT_GEM = ITEMS.register("sealed_artifact_gem",
            () -> new SealedArtifactItem(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));

    public static final DeferredItem<BlockItem> MYSTICAL_RING = ITEMS.register("mystical_ring",
            () -> new BlockItem(ModBlocks.MYSTICAL_RING.get(), new Item.Properties())
    );

    public static PotionIngredient selectRandomIngredient(List<PotionIngredient> ingredients, Random random) {
        if (ingredients == null || ingredients.isEmpty()) {
            return null;
        }

        Map<PotionIngredient, Integer> weights = new HashMap<>();
        int totalWeight = 0;

        for (PotionIngredient ingredient : ingredients) {
            int weight = ingredient.getSequence() + 1;
            weights.put(ingredient, weight);
            totalWeight += weight;
        }

        int randomValue = random.nextInt(totalWeight);

        int cumulativeWeight = 0;
        for (Map.Entry<PotionIngredient, Integer> entry : weights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue < cumulativeWeight) {
                return entry.getKey();
            }
        }

        return ingredients.getLast();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}