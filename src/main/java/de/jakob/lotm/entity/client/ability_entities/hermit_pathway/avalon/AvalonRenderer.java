package de.jakob.lotm.entity.client.ability_entities.hermit_pathway.avalon;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jakob.lotm.entity.custom.ability_entities.hermit_pathway.AvalonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AvalonRenderer extends EntityRenderer<AvalonEntity> {

    public AvalonRenderer(EntityRendererProvider.Context context) {
            super(context);
    }

    @Override
    public void render(AvalonEntity entity, float entityYaw, float partialTicks,
                           PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            // Intentionally empty — Avalon is invisible
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AvalonEntity entity) {
            return ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    }
}
