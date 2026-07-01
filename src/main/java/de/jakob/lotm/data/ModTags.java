package de.jakob.lotm.data;

import de.jakob.lotm.LOTMCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModTags {
    public static class Structures {
        public static final TagKey<Structure> HIDEOUT =
            TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "beyonder_hideout"));
        public static final TagKey<Structure> UNIQUENESS_TEMPLE =
            TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "uniqueness_temple"));
    }
}