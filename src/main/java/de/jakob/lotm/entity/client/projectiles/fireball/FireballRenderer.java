package de.jakob.lotm.entity.client.projectiles.fireball;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.custom.projectiles.FireballEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class FireballRenderer extends EntityRenderer<FireballEntity, FireballRenderState> {

    private final FireballModel model;

    public FireballRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FireballModel(context.bakeLayer(FireballModel.LAYER_LOCATION));
    }

    @Override
    public FireballRenderState createRenderState() {
        return new FireballRenderState();
    }

    @Override
    public void extractRenderState(FireballEntity entity, FireballRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.size = entity.getSize();
        state.petrified = entity.getTags().contains("petrified");
        state.texture = this.getTextureLocation(entity);
        state.yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(FireballRenderState state, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector collector, net.minecraft.client.renderer.state.CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.scale(state.size, state.size, state.size);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot + 90.0F));

        RenderType renderType = state.petrified ? this.model.renderType(LOTMCraft.STONE_TEXTURE) :
                this.model.renderType(state.texture);

        int color = state.petrified ? 0xFF808080 : 0xFFFFFFFF;
        collector.order(0).submitModel(this.model, state, poseStack, renderType, 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, color, null);

        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraRenderState);
    }

    protected int getBlockLightLevel(FireballEntity projectileEntity, BlockPos blockpos) {
        return 15;
    }

    @Override
    public Identifier getTextureLocation(FireballRenderState state) {
        return Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "textures/entity/fireball/fireball.png");
    }
}