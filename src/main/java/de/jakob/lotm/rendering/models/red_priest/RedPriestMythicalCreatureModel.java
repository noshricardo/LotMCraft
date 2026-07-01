package de.jakob.lotm.rendering.models.red_priest;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.jakob.lotm.LOTMCraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class RedPriestMythicalCreatureModel<T extends Entity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LOTMCraft.MOD_ID, "red_priest_mythical_creature"), "main");
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart headflames;
	private final ModelPart skull;
	private final ModelPart body;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart cape;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	private final ModelPart tassels;
	private final ModelPart l_tassels;
	private final ModelPart r_tassels;

	public RedPriestMythicalCreatureModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.headflames = this.head.getChild("headflames");
		this.skull = this.head.getChild("skull");
		this.body = root.getChild("body");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.cape = this.right_arm.getChild("cape");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
		this.tassels = root.getChild("tassels");
		this.l_tassels = this.tassels.getChild("l_tassels");
		this.r_tassels = this.tassels.getChild("r_tassels");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0677F, -13.7865F, 6.4862F, 0.1865F, 0.133F, -0.1243F));

		PartDefinition headflames = head.addOrReplaceChild("headflames", CubeListBuilder.create().texOffs(12, 111).addBox(-2.3646F, -3.8721F, -2.0393F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-0.0677F, -0.7865F, 1.4862F, -1.0908F, 0.0F, 0.0F));

		PartDefinition headwear_r1 = headflames.addOrReplaceChild("headwear_r1", CubeListBuilder.create().texOffs(27, 111).addBox(-3.5F, -4.5F, -3.5F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.6354F, -1.3721F, -1.0393F, 0.1745F, 0.0F, 0.0F));

		PartDefinition headwear_r2 = headflames.addOrReplaceChild("headwear_r2", CubeListBuilder.create().texOffs(25, 111).addBox(-2.5F, -2.5F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-3.3287F, 0.4678F, 0.0466F, 0.0877F, -0.2022F, -0.5855F));

		PartDefinition headwear_r3 = headflames.addOrReplaceChild("headwear_r3", CubeListBuilder.create().texOffs(25, 111).addBox(-2.5F, -1.5F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(3.5938F, 0.3496F, 0.6102F, -0.0883F, 0.2325F, 0.5475F));

		PartDefinition skull = head.addOrReplaceChild("skull", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0677F, 0.7865F, -1.4862F, -0.1309F, 0.0F, 0.0F));

		PartDefinition tassel_r1 = skull.addOrReplaceChild("tassel_r1", CubeListBuilder.create().texOffs(48, 18).addBox(0.0472F, -5.3647F, 1.2045F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0774F, -2.5937F, -2.7402F, -0.5025F, 2.1761F));

		PartDefinition tassel_r2 = skull.addOrReplaceChild("tassel_r2", CubeListBuilder.create().texOffs(48, 18).addBox(0.0472F, -5.3647F, -1.2045F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0774F, -2.5937F, -0.4014F, -0.5025F, 0.9655F));

		PartDefinition head_r1 = skull.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -2.0F, -3.0F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.0243F, 2.5912F, -0.1745F, 0.0F, 0.0F));

		PartDefinition tassel_r3 = body.addOrReplaceChild("tassel_r3", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1F, -9.0531F, -2.1849F, -2.6345F, -0.3567F, 2.1319F));

		PartDefinition tassel_r4 = body.addOrReplaceChild("tassel_r4", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1F, -9.0531F, -2.1849F, -0.5071F, -0.3567F, 1.0096F));

		PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(7, 114).addBox(-3.5F, -4.4208F, -1.9056F, 7.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.945F, -0.1856F, -2.9671F, 0.0F, 0.0F));

		PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, 112).addBox(-4.0F, -4.5747F, -2.0694F, 9.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.001F, -0.3218F, -0.2182F, 0.0F, 0.0F));

		PartDefinition body_r3 = body.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(90, 2).addBox(-4.0F, -5.5F, -2.0F, 8.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5243F, -0.5912F, 0.1745F, 0.0F, 0.0F));

		PartDefinition body_r4 = body.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(56, 0).addBox(-5.0F, -8.5F, -3.0F, 10.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5243F, -1.5912F, -0.2182F, 0.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(10, 14).mirror().addBox(-1.4233F, -12.0112F, -0.3407F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(20, 114).mirror().addBox(-0.9233F, -11.0112F, 0.1593F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(12.2766F, -9.1617F, -2.4751F, -1.7525F, -0.9066F, -0.194F));

		PartDefinition tassel_r5 = left_arm.addOrReplaceChild("tassel_r5", CubeListBuilder.create().texOffs(48, 18).addBox(-0.9384F, -4.7044F, 0.0027F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3233F, 1.4114F, 3.5657F, -1.6074F, 0.6973F, 1.5575F));

		PartDefinition tassel_r6 = left_arm.addOrReplaceChild("tassel_r6", CubeListBuilder.create().texOffs(48, 18).addBox(-2.5569F, -5.4319F, -0.1602F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3233F, 1.4114F, 3.5657F, -1.6007F, 0.0419F, 1.5361F));

		PartDefinition tassel_r7 = left_arm.addOrReplaceChild("tassel_r7", CubeListBuilder.create().texOffs(48, 18).addBox(0.7377F, -5.2167F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3233F, 1.4114F, 3.5657F, -1.5708F, 1.3963F, 1.6144F));

		PartDefinition left_arm_r1 = left_arm.addOrReplaceChild("left_arm_r1", CubeListBuilder.create().texOffs(10, 116).mirror().addBox(-1.0F, -4.5F, -0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.0211F, -0.0734F, -0.6919F, 2.4477F, 0.084F, 0.1005F));

		PartDefinition left_arm_r2 = left_arm.addOrReplaceChild("left_arm_r2", CubeListBuilder.create().texOffs(0, 111).mirror().addBox(-1.6787F, -1.5F, -0.9783F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.2446F, 3.3846F, -4.2623F, -0.6939F, 0.084F, 0.1005F));

		PartDefinition left_arm_r3 = left_arm.addOrReplaceChild("left_arm_r3", CubeListBuilder.create().texOffs(28, 112).mirror().addBox(-2.6787F, -2.5F, -1.9783F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.2446F, 4.3846F, -4.2623F, -0.6939F, 0.084F, 0.1005F));

		PartDefinition left_arm_r4 = left_arm.addOrReplaceChild("left_arm_r4", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.5F, -4.5F, -1.0F, 3.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0767F, -0.5112F, -0.3407F, -0.6939F, 0.084F, 0.1005F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0384F, -4.4154F, 0.5181F, -0.1198F, -0.1831F, 1.5067F));

		PartDefinition right_arm_r1 = right_arm.addOrReplaceChild("right_arm_r1", CubeListBuilder.create().texOffs(17, 116).addBox(-12.4575F, -13.2908F, -2.3265F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.4051F, 12.993F, 1.9043F, 0.5881F, 0.2341F, 0.3642F));

		PartDefinition right_arm_r2 = right_arm.addOrReplaceChild("right_arm_r2", CubeListBuilder.create().texOffs(0, 111).addBox(-13.3494F, 12.1515F, 0.4234F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-13.0346F, 3.741F, 0.8265F, 3.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.4051F, 12.993F, 1.9043F, -2.5534F, 0.2341F, 0.3642F));

		PartDefinition right_arm_r3 = right_arm.addOrReplaceChild("right_arm_r3", CubeListBuilder.create().texOffs(34, 114).addBox(-12.7332F, -4.2724F, -5.2291F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(10, 14).addBox(-13.2763F, -5.2724F, -5.7291F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0209F, 5.42F, 6.0801F, -0.4297F, -0.002F, 0.3388F));

		PartDefinition cape = right_arm.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.offset(5.8973F, -3.2805F, 1.7769F));

		PartDefinition tassel_r8 = cape.addOrReplaceChild("tassel_r8", CubeListBuilder.create().texOffs(40, 112).addBox(-5.0509F, 0.7572F, 0.999F, 11.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8607F, 16.3499F, -8.0223F, -2.6417F, -0.9853F, 1.3653F));

		PartDefinition tassel_r9 = cape.addOrReplaceChild("tassel_r9", CubeListBuilder.create().texOffs(106, 88).addBox(-5.882F, -1.2301F, 0.9125F, 11.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8607F, 16.3499F, -8.0223F, 2.5499F, 1.0377F, -1.6284F));

		PartDefinition tassel_r10 = cape.addOrReplaceChild("tassel_r10", CubeListBuilder.create().texOffs(40, 112).addBox(-5.0509F, 0.7572F, 0.999F, 11.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8607F, 7.3499F, -12.0223F, -2.8926F, -0.0887F, 1.629F));

		PartDefinition tassel_r11 = cape.addOrReplaceChild("tassel_r11", CubeListBuilder.create().texOffs(106, 88).addBox(-5.882F, -1.2301F, 0.9125F, 11.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8607F, 7.3499F, -12.0223F, 2.6847F, 0.125F, -1.4251F));

		PartDefinition tassel_r12 = cape.addOrReplaceChild("tassel_r12", CubeListBuilder.create().texOffs(40, 112).addBox(-8.528F, 2.4166F, -0.3848F, 11.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1236F, 8.7005F, 4.3032F, -0.1814F, -0.4456F, -1.2267F));

		PartDefinition tassel_r13 = cape.addOrReplaceChild("tassel_r13", CubeListBuilder.create().texOffs(106, 108).addBox(-2.645F, -2.586F, -0.9839F, 11.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1236F, 8.7005F, 4.3032F, -0.0368F, 0.4456F, 1.9149F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(4.6108F, 15.4244F, 1.4571F, -0.3962F, -0.7344F, 0.0896F));

		PartDefinition tassel_r14 = left_leg.addOrReplaceChild("tassel_r14", CubeListBuilder.create().texOffs(48, 18).addBox(-3.5255F, -6.9405F, 1.148F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5385F, 0.7655F, 0.5486F, -0.2553F, -1.2797F, 0.7635F));

		PartDefinition tassel_r15 = left_leg.addOrReplaceChild("tassel_r15", CubeListBuilder.create().texOffs(48, 18).addBox(2.9244F, -3.1265F, -0.4234F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5385F, 0.7655F, 0.5486F, -0.8884F, -0.7228F, 1.4975F));

		PartDefinition left_leg_r1 = left_leg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(17, 114).mirror().addBox(-1.0F, -5.5F, -0.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.2566F, 2.4737F, -0.6263F, -2.7595F, 0.0049F, -0.0594F));

		PartDefinition left_leg_r2 = left_leg.addOrReplaceChild("left_leg_r2", CubeListBuilder.create().texOffs(36, 13).mirror().addBox(2.4864F, 3.1602F, -4.0797F, 2.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0655F, 2.5163F, -0.6783F, 0.6875F, 0.0049F, -0.0594F));

		PartDefinition left_leg_r3 = left_leg.addOrReplaceChild("left_leg_r3", CubeListBuilder.create().texOffs(38, 16).mirror().addBox(1.4864F, -5.8398F, -1.0797F, 3.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0655F, 2.5163F, -0.6783F, 0.3821F, 0.0049F, -0.0594F));

		PartDefinition left_leg_r4 = left_leg.addOrReplaceChild("left_leg_r4", CubeListBuilder.create().texOffs(25, 14).mirror().addBox(-2.0F, -8.5F, -3.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(6, 115).mirror().addBox(-1.5F, -8.0F, -1.5F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.9159F, 0.0756F, -0.1879F, -0.1309F, 0.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.5411F, 16.2077F, 4.5646F, -0.0702F, 0.2515F, 0.2199F));

		PartDefinition tassel_r16 = right_leg.addOrReplaceChild("tassel_r16", CubeListBuilder.create().texOffs(48, 18).addBox(1.4293F, -6.7321F, 0.9341F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1683F, 0.7248F, 1.4961F, -1.0446F, 1.3825F, -1.5501F));

		PartDefinition tassel_r17 = right_leg.addOrReplaceChild("tassel_r17", CubeListBuilder.create().texOffs(48, 18).addBox(-4.5821F, -2.8994F, 0.4422F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1683F, 0.7248F, 1.4961F, -1.1321F, 0.752F, -1.4555F));

		PartDefinition right_leg_r1 = right_leg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(28, 115).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9004F, 2.3128F, -0.37F, -2.757F, -0.0044F, 0.0522F));

		PartDefinition right_leg_r2 = right_leg.addOrReplaceChild("right_leg_r2", CubeListBuilder.create().texOffs(35, 13).addBox(-4.4878F, 3.19F, -3.9452F, 2.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(38, 16).addBox(-4.4878F, -5.81F, -0.9452F, 3.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8838F, 1.3815F, -0.8465F, 0.3845F, -0.0044F, 0.0522F));

		PartDefinition right_leg_r3 = right_leg.addOrReplaceChild("right_leg_r3", CubeListBuilder.create().texOffs(25, 14).addBox(-2.0F, -9.5F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(6, 115).addBox(-0.5F, -8.0F, -0.5F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6922F, -1.0592F, -1.2322F, -0.1309F, 0.0F, 0.0F));

		PartDefinition tassels = partdefinition.addOrReplaceChild("tassels", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 1.0F));

		PartDefinition l_tassels = tassels.addOrReplaceChild("l_tassels", CubeListBuilder.create(), PartPose.offsetAndRotation(7.1F, -23.4107F, 4.4063F, 0.0F, 0.3927F, 0.0F));

		PartDefinition tassel_r18 = l_tassels.addOrReplaceChild("tassel_r18", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6667F, 2.0F, -1.5708F, 0.0F, 1.5708F));

		PartDefinition tassel_r19 = l_tassels.addOrReplaceChild("tassel_r19", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3333F, 0.0F, -1.5708F, 0.48F, 1.5708F));

		PartDefinition tassel_r20 = l_tassels.addOrReplaceChild("tassel_r20", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.3333F, -2.0F, -1.5708F, 0.7854F, 1.5708F));

		PartDefinition r_tassels = tassels.addOrReplaceChild("r_tassels", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.1F, -23.5F, -0.5F, 0.0F, -0.3927F, 0.0F));

		PartDefinition tassel_r21 = r_tassels.addOrReplaceChild("tassel_r21", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5774F, 6.9063F, -1.5708F, 0.0F, -1.5708F));

		PartDefinition tassel_r22 = r_tassels.addOrReplaceChild("tassel_r22", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.4226F, 4.9063F, -1.5708F, -0.48F, -1.5708F));

		PartDefinition tassel_r23 = r_tassels.addOrReplaceChild("tassel_r23", CubeListBuilder.create().texOffs(48, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.4226F, 2.9063F, -1.5708F, -0.7854F, -1.5708F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		tassels.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return null;
	}
}