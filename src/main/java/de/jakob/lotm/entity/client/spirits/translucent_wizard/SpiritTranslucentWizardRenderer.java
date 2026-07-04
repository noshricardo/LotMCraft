package de.jakob.lotm.entity.client.spirits.translucent_wizard;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.custom.spirits.SpiritTranslucentWizardEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class SpiritTranslucentWizardRenderer extends MobRenderer<SpiritTranslucentWizardEntity, SpiritTranslucentWizardRenderState, SpiritTranslucentWizardModel<SpiritTranslucentWizardRenderState>> {
    public SpiritTranslucentWizardRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritTranslucentWizardModel<>(context.bakeLayer(SpiritTranslucentWizardModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritTranslucentWizardRenderState createRenderState() {
        return new SpiritTranslucentWizardRenderState();
    }

    @Override
    public void extractRenderState(SpiritTranslucentWizardEntity entity, SpiritTranslucentWizardRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.walkAnimationState.copyFrom(entity.WALK_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.isWalking = entity.isFlying() || entity.getDeltaMovement().horizontalDistance() > 0.01;
    }

    @Override
    public Identifier getTextureLocation(SpiritTranslucentWizardRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_blue_wizard/spirit_blue_wizard.png");
    }
}
