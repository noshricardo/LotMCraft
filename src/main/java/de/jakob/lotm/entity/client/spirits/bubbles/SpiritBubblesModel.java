package de.jakob.lotm.entity.client.spirits.bubbles;// Made with Blockbench 5.1.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.dervish.SpiritDervishAnimations;
import de.jakob.lotm.entity.custom.spirits.SpiritBubblesEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SpiritBubblesModel extends EntityModel<SpiritBubblesRenderState> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "spirit_bubbles"), "main");
	private final ModelPart bubbles;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;
	private final KeyframeAnimation idleAnimation;

	public SpiritBubblesModel(ModelPart root) {
		super(root);
		this.bubbles = root.getChild("bubbles");
		this.bone = this.bubbles.getChild("bone");
		this.bone2 = this.bubbles.getChild("bone2");
		this.bone3 = this.bubbles.getChild("bone3");
		this.idleAnimation = SpiritBubblesAnimations.IDLE.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bubbles = partdefinition.addOrReplaceChild("bubbles", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 0.0F));

		PartDefinition bone = bubbles.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.5F, 0.0F));

		PartDefinition bone2 = bubbles.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0846F, 2.1088F, 2.2588F));

		PartDefinition head_r1 = bone2.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.5F, -8.0F, -3.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0846F, 6.8912F, -1.2588F, -0.2572F, -0.8124F, -0.0723F));

		PartDefinition bone3 = bubbles.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(-1.5F, 5.0F, -0.5F));

		PartDefinition head_r2 = bone3.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4013F, 0.2013F, 0.4773F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(SpiritBubblesRenderState state) {
		super.setupAnim(state);
		this.applyHeadRotation(state.yRot, state.xRot);

		this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1f);
	}

	private void applyHeadRotation(float yRot, float xRot) {
		yRot = Mth.clamp(yRot, -30f, 30f);
		xRot = Mth.clamp(xRot, -25f, 45);

		this.bubbles.yRot = yRot * ((float)Math.PI / 180f);
		this.bubbles.xRot = xRot *  ((float)Math.PI / 180f);
	}

	// renderToBuffer and root() are now final in Model/EntityModel and should not be overridden.
	// The root model part is already handled by the super constructor.
	// Individual parts should be rendered in a custom way if needed, 
	// but normally EntityRenderer handles the call to model.render() which is now final and calls root.render()
}