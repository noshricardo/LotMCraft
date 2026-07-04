package de.jakob.lotm.entity.client.uniqueness;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.custom.uniqueness.UniquenessEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renders the UniquenessEntity as a floating, slowly rotating item on the ground,
 * using the corresponding pathway uniqueness item texture.
 */
@OnlyIn(Dist.CLIENT)
public class UniquenessEntityRenderer extends EntityRenderer<UniquenessEntity, UniquenessEntityRenderState> {

    public UniquenessEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public UniquenessEntityRenderState createRenderState() {
        return new UniquenessEntityRenderState();
    }

    @Override
    public void extractRenderState(UniquenessEntity entity, UniquenessEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        String pathway = entity.getPathway();
        if (!pathway.isEmpty()) {
            state.itemStack = getUniquenessItemStack(pathway);
        } else {
            state.itemStack = ItemStack.EMPTY;
        }
        state.tick = entity.tickCount + partialTick;
    }

    private ItemStack getUniquenessItemStack(String pathway) {
        try {
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                    Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, pathway + "_uniqueness")
            ).map(net.minecraft.core.Holder.Reference::value).orElse(Items.AIR);
            if (item == Items.AIR) return ItemStack.EMPTY;
            return new ItemStack(item);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public Identifier getTextureLocation(UniquenessEntityRenderState state) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
