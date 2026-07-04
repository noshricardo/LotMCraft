package de.jakob.lotm.entity.client.spirits.blue_wizard;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.bubbles.SpiritBubblesModel;
import de.jakob.lotm.entity.custom.spirits.SpiritBlueWizardEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritBubblesEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SpiritBlueWizardRenderer extends MobRenderer<SpiritBlueWizardEntity, SpiritBlueWizardRenderState, SpiritBlueWizardModel<SpiritBlueWizardRenderState>> {
    public SpiritBlueWizardRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritBlueWizardModel<>(context.bakeLayer(SpiritBlueWizardModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritBlueWizardRenderState createRenderState() {
        return new SpiritBlueWizardRenderState();
    }

    @Override
    public void extractRenderState(SpiritBlueWizardEntity entity, SpiritBlueWizardRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.walkAnimationState.copyFrom(entity.WALK_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.isWalking = entity.isFlying() || entity.getDeltaMovement().horizontalDistance() > 0.01;
    }

    @Override
    public Identifier getTextureLocation(SpiritBlueWizardRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_blue_wizard/spirit_blue_wizard.png");
    }
}
