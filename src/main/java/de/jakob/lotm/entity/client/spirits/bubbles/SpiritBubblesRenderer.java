package de.jakob.lotm.entity.client.spirits.bubbles;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.dervish.SpiritDervishModel;
import de.jakob.lotm.entity.custom.spirits.SpiritBubblesEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritDervishEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SpiritBubblesRenderer extends MobRenderer<SpiritBubblesEntity, SpiritBubblesRenderState, SpiritBubblesModel> {
    public SpiritBubblesRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritBubblesModel(context.bakeLayer(SpiritBubblesModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritBubblesRenderState createRenderState() {
        return new SpiritBubblesRenderState();
    }

    @Override
    public void extractRenderState(SpiritBubblesEntity entity, SpiritBubblesRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.headYaw = entity.getViewYRot(partialTicks);
        state.headPitch = entity.getViewXRot(partialTicks);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
    }

    @Override
    public Identifier getTextureLocation(SpiritBubblesRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_bubbles/spirit_bubbles.png");
    }

    @Override
    public void render(SpiritBubblesRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0, -.32, 0);

        // In 1.21.4, we should ideally put scale in the state if it depends on the entity.
        // But for now, we can still use random if we have a seed in the state.
        // Actually, state.entityId or something?
        
        super.render(state, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(SpiritBubblesRenderState state, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(this.getTextureLocation(state));
    }
}
