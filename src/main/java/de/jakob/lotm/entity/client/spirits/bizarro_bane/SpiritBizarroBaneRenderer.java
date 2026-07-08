package de.jakob.lotm.entity.client.spirits.bizarro_bane;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.blue_wizard.SpiritBlueWizardModel;
import de.jakob.lotm.entity.custom.spirits.SpiritBizarroBaneEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritBlueWizardEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SpiritBizarroBaneRenderer extends MobRenderer<SpiritBizarroBaneEntity, SpiritBizarroBaneRenderState, SpiritBizarroBaneModel<SpiritBizarroBaneRenderState>> {
    public SpiritBizarroBaneRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritBizarroBaneModel<>(context.bakeLayer(SpiritBizarroBaneModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritBizarroBaneRenderState createRenderState() {
        return new SpiritBizarroBaneRenderState();
    }

    @Override
    public void extractRenderState(SpiritBizarroBaneEntity entity, SpiritBizarroBaneRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isWalking = entity.walkAnimation.isMoving();
        state.walkAnimationState.copyFrom(entity.WALK_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
    }

    @Override
    public Identifier getTextureLocation(SpiritBizarroBaneRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_bizarro_bane/spirit_bizarro_bane.png");
    }
}
