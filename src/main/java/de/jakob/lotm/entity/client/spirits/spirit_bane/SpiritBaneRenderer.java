package de.jakob.lotm.entity.client.spirits.spirit_bane;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.bizarro_bane.SpiritBizarroBaneModel;
import de.jakob.lotm.entity.custom.spirits.SpiritBaneEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritBizarroBaneEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SpiritBaneRenderer extends MobRenderer<SpiritBaneEntity, SpiritBaneRenderState, SpiritBaneModel<SpiritBaneRenderState>> {
    public SpiritBaneRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritBaneModel<>(context.bakeLayer(SpiritBaneModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritBaneRenderState createRenderState() {
        return new SpiritBaneRenderState();
    }

    @Override
    public void extractRenderState(SpiritBaneEntity entity, SpiritBaneRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.walkAnimationState.copyFrom(entity.WALK_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.isWalking = entity.walkAnimation.speed() > 0.01f;
    }

    @Override
    public Identifier getTextureLocation(SpiritBaneRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_bane/spirit_bane.png");
    }
}
