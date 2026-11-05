// Made with Blockbench 4.12.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class shardblade<T extends Trident> extends EntityModel<T>
{
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "shardblade"), "main");
	private final ModelPart root;
	private final ModelPart blade;
	private final ModelPart blade_guide;
	private final ModelPart blade_1;
	private final ModelPart blade_2;
	private final ModelPart handle;
	private final ModelPart handle_guide;
	private final ModelPart handle_1;
	private final ModelPart pommel;
	private final ModelPart pommel_guide;
	private final ModelPart pommel_1;
	private final ModelPart cross_guard;
	private final ModelPart crossguard_guide;
	private final ModelPart crossguard_1;

	public shardblade(ModelPart root)
	{
		this.root = root.getChild("root");
		this.blade = this.root.getChild("blade");
		this.blade_guide = this.blade.getChild("blade_guide");
		this.blade_1 = this.blade.getChild("blade_1");
		this.blade_2 = this.blade.getChild("blade_2");
		this.handle = this.root.getChild("handle");
		this.handle_guide = this.handle.getChild("handle_guide");
		this.handle_1 = this.handle.getChild("handle_1");
		this.pommel = this.root.getChild("pommel");
		this.pommel_guide = this.pommel.getChild("pommel_guide");
		this.pommel_1 = this.pommel.getChild("pommel_1");
		this.cross_guard = this.root.getChild("cross_guard");
		this.crossguard_guide = this.cross_guard.getChild("crossguard_guide");
		this.crossguard_1 = this.cross_guard.getChild("crossguard_1");
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition blade = root.addOrReplaceChild("blade", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition blade_guide = blade.addOrReplaceChild("blade_guide", CubeListBuilder.create().texOffs(19, 3).addBox(-2.5F, -45.0F, -0.5F, 5.0F, 28.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition blade_1 = blade.addOrReplaceChild("blade_1", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -45.0F, -0.5F, 1.0F, 28.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition blade_2 = blade.addOrReplaceChild("blade_2", CubeListBuilder.create().texOffs(19, 3).addBox(-2.5F, -45.0F, -0.5F, 5.0F, 28.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition handle = root.addOrReplaceChild("handle", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition handle_guide = handle.addOrReplaceChild("handle_guide", CubeListBuilder.create().texOffs(4, 8).addBox(-0.5F, -16.0F, -0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition handle_1 = handle.addOrReplaceChild("handle_1", CubeListBuilder.create().texOffs(4, 8).addBox(-0.5F, -16.0F, -0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pommel = root.addOrReplaceChild("pommel", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pommel_guide = pommel.addOrReplaceChild("pommel_guide", CubeListBuilder.create().texOffs(4, 3).addBox(-1.5F, -9.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pommel_1 = pommel.addOrReplaceChild("pommel_1", CubeListBuilder.create().texOffs(4, 3).addBox(-1.5F, -9.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cross_guard = root.addOrReplaceChild("cross_guard", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition crossguard_guide = cross_guard.addOrReplaceChild("crossguard_guide", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -17.0F, -1.0F, 9.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition crossguard_1 = cross_guard.addOrReplaceChild("crossguard_1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -17.0F, -1.0F, 9.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Trident entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
