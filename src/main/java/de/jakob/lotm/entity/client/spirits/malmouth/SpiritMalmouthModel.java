package de.jakob.lotm.entity.client.spirits.malmouth;// Made with Blockbench 5.1.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.translucent_wizard.SpiritTranslucentWizardAnimations;
import de.jakob.lotm.entity.custom.spirits.SpiritMalmouthEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SpiritMalmouthModel<T extends SpiritMalmouthRenderState> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "spirit_malmouth"), "main");
	private final ModelPart head;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation idleAnimation;

	public SpiritMalmouthModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.walkAnimation = SpiritMalmouthAnimations.WALK.bake(root);
		this.idleAnimation = SpiritMalmouthAnimations.IDLE.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 14.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T state) {
		super.setupAnim(state);
		this.applyHeadRotation(state.yRot, state.xRot);

		if (state.isFlying) {
			this.walkAnimation.apply(state.walkAnimationState, state.ageInTicks, 1.0F);
		} else {
			this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1.0F);
		}
	}

	private void applyHeadRotation(float yRot, float xRot) {
		yRot = Mth.clamp(yRot, -30f, 30f);
		xRot = Mth.clamp(xRot, -25f, 45);

		this.head.yRot = yRot * ((float)Math.PI / 180f);
		this.head.xRot = xRot *  ((float)Math.PI / 180f);
	}

}