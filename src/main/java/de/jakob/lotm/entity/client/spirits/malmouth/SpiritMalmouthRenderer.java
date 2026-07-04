package de.jakob.lotm.entity.client.spirits.malmouth;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.ghost.SpiritGhostModel;
import de.jakob.lotm.entity.custom.spirits.SpiritGhostEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritMalmouthEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SpiritMalmouthRenderer extends MobRenderer<SpiritMalmouthEntity, SpiritMalmouthRenderState, SpiritMalmouthModel<SpiritMalmouthRenderState>> {
    public SpiritMalmouthRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritMalmouthModel<>(context.bakeLayer(SpiritMalmouthModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritMalmouthRenderState createRenderState() {
        return new SpiritMalmouthRenderState();
    }

    @Override
    public void extractRenderState(SpiritMalmouthEntity entity, SpiritMalmouthRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.walkAnimationState.copyFrom(entity.WALK_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.isFlying = entity.isFlying();
    }

    @Override
    public Identifier getTextureLocation(SpiritMalmouthRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_malmouth/spirit_malmouth.png");
    }
}
