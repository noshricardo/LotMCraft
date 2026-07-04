package de.jakob.lotm.entity.client.spirits.dervish;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.custom.spirits.SpiritDervishEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

import java.util.Random;

public class SpiritDervishRenderer extends MobRenderer<SpiritDervishEntity, SpiritDervishRenderState, SpiritDervishModel<SpiritDervishRenderState>> {
    public SpiritDervishRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritDervishModel<>(context.bakeLayer(SpiritDervishModel.LAYER_LOCATION)), .3f);
    }

    @Override
    public SpiritDervishRenderState createRenderState() {
        return new SpiritDervishRenderState();
    }

    @Override
    public void extractRenderState(SpiritDervishEntity entity, SpiritDervishRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.idleAnimationState.copyFrom(entity.IDLE_ANIMATION);
        state.mostSignificantBits = entity.getUUID().getMostSignificantBits();
        state.leastSignificantBits = entity.getUUID().getLeastSignificantBits();
    }

    @Override
    public Identifier getTextureLocation(SpiritDervishRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/spirits/spirit_dervish/spirit_dervish.png");
    }
}
