package de.jakob.lotm.entity.client.spirits.bubbles;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.dervish.SpiritDervishModel;
import de.jakob.lotm.entity.custom.spirits.SpiritBubblesEntity;
import de.jakob.lotm.entity.custom.spirits.SpiritDervishEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
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
        // LivingEntityRenderState already has xRot and yRot which correspond to head rotation by default in many cases.
        // We can override or set them here if needed.
        state.yRot = entity.getViewYRot(partialTicks);
        state.xRot = entity.getViewXRot(partialTicks);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
    }

    @Override
    public Identifier getTextureLocation(SpiritBubblesRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_bubbles/spirit_bubbles.png");
    }
}
