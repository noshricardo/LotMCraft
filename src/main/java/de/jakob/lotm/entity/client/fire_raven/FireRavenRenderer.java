package de.jakob.lotm.entity.client.fire_raven;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.custom.FireRavenEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class FireRavenRenderer extends MobRenderer<FireRavenEntity, FireRavenRenderState, FireRavenModel<FireRavenRenderState>> {
    public FireRavenRenderer(EntityRendererProvider.Context context) {
        super(context, new FireRavenModel<>(context.bakeLayer(FireRavenModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public FireRavenRenderState createRenderState() {
        return new FireRavenRenderState();
    }

    @Override
    public void extractRenderState(FireRavenEntity entity, FireRavenRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.flyAnimationState.copyFrom(entity.FLY_ANIMATION);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.isFlying = entity.isFlying();
        state.walkLimbSwing = entity.walkAnimation.position(partialTicks);
        state.walkLimbSwingAmount = entity.walkAnimation.speed(partialTicks);
    }

    @Override
    public Identifier getTextureLocation(FireRavenRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/fire_raven/fire_raven.png");
    }
}
