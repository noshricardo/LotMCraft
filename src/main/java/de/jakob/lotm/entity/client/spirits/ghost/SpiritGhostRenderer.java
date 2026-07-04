package de.jakob.lotm.entity.client.spirits.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.dervish.SpiritDervishModel;
import de.jakob.lotm.entity.custom.spirits.SpiritDervishEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritGhostEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SpiritGhostRenderer extends MobRenderer<SpiritGhostEntity, SpiritGhostRenderState, SpiritGhostModel<SpiritGhostRenderState>> {
    public SpiritGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritGhostModel<>(context.bakeLayer(SpiritGhostModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritGhostRenderState createRenderState() {
        return new SpiritGhostRenderState();
    }

    @Override
    public void extractRenderState(SpiritGhostEntity entity, SpiritGhostRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.walkAnimationState.copyFrom(entity.WALK_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.isFlying = entity.isFlying();
    }

    @Override
    public Identifier getTextureLocation(SpiritGhostRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_ghost/spirit_ghost.png");
    }
}
