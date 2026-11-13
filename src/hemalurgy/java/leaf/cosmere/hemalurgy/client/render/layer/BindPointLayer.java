/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  leaf.cosmere.common.Cosmere
 *  leaf.cosmere.hemalurgy.client.render.VisionClientState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.model.geom.builders.PartDefinition
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.entity.LivingEntity
 */
package leaf.cosmere.hemalurgy.client.render.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import leaf.cosmere.common.Cosmere;
import leaf.cosmere.hemalurgy.client.render.BindPointClientState;
import leaf.cosmere.hemalurgy.client.render.VisionClientState;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointState;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointTable;
import leaf.cosmere.hemalurgy.common.BindPointGeometry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BindPointLayer
extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>> {
    private static final RenderType TEXTURE = BindPointRenderType.INSTANCE;
    private static final Map<String, float[]> CATEGORY_TINTS = Map.of("mental", new float[]{0.3f, 0.5f, 1.0f}, "physical", new float[]{1.0f, 0.3f, 0.3f}, "spiritual", new float[]{1.0f, 0.85f, 0.2f}, "temporal", new float[]{0.8f, 0.3f, 1.0f});
    private final Map<String, ModelPart> cubes = new HashMap<String, ModelPart>();

    public BindPointLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderer) {
        super(renderer);
        for (String key : BindPointGeometry.keys()) {
            BindPointGeometry.CubeDef def = BindPointGeometry.cube(key);
            this.cubes.put(key, BindPointLayer.createCube(def.x(), def.y(), def.z()));
        }
    }

    private static ModelPart createCube(float cx, float cy, float cz) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("cube", CubeListBuilder.create().addBox(cx - 0.5f, cy - 0.5f, cz - 0.5f, 1.0f, 1.0f, 1.0f), PartPose.ZERO);
        return LayerDefinition.create((MeshDefinition)mesh, (int)16, (int)16).bakeRoot().getChild("cube");
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean menuActive = BindPointState.active;
        boolean renderCubes = menuActive || VisionClientState.isActive((LocalPlayer)Minecraft.getInstance().player);
        HumanoidModel model = (HumanoidModel)this.getParentModel();
        VertexConsumer consumer = renderCubes ? buffer.getBuffer(TEXTURE) : null;
        Set<String> keys = menuActive ? BindPointState.current : BindPointTable.keys();
        for (String key : keys) {
            BindPointGeometry.CubeDef def = BindPointGeometry.cube(key);
            ModelPart cube = this.cubes.get(key);
            if (def == null || cube == null) continue;
            ModelPart group = groupFor(model, def);
            if (group == null) continue;
            cube.copyFrom(group);
            poseStack.pushPose();
            cube.translateAndRotate(poseStack);
            Vector4f view = new Vector4f(def.x() / 16.0f, def.y() / 16.0f, def.z() / 16.0f, 1.0f);
            view.mul(poseStack.last().pose());
            poseStack.popPose();
            Vector3f offset = new Vector3f(view.x, view.y, view.z);
            RenderSystem.getInverseViewRotationMatrix().transform(offset);
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            BindPointClientState.put(entity.getId(), key, new Vec3(offset.x, offset.y, offset.z).add(camPos));
            if (!renderCubes) continue;
            float[] tint = CATEGORY_TINTS.get(key.substring(0, key.indexOf(58)));
            VertexConsumer tinted = tint == null ? consumer : new TintedVertexConsumer(consumer, tint[0], tint[1], tint[2]);
            cube.render(poseStack, tinted, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private static ModelPart groupFor(HumanoidModel<?> model, BindPointGeometry.CubeDef def) {
        return switch (def.group()) {
            case "head" -> model.head;
            case "body" -> model.body;
            case "leftArm" -> model.leftArm;
            case "rightArm" -> model.rightArm;
            case "leftLeg" -> model.leftLeg;
            case "rightLeg" -> model.rightLeg;
            default -> null;
        };
    }

    private static final class TintedVertexConsumer
    implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float r;
        private final float g;
        private final float b;

        private TintedVertexConsumer(VertexConsumer delegate, float r, float g, float b) {
            this.delegate = delegate;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        private static int scale(int value, float factor) {
            return Math.min(255, Math.round((float)value * factor));
        }

        public VertexConsumer vertex(double x, double y, double z) {
            return this.delegate.vertex(x, y, z);
        }

        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this.delegate.color(TintedVertexConsumer.scale(red, this.r), TintedVertexConsumer.scale(green, this.g), TintedVertexConsumer.scale(blue, this.b), alpha);
        }

        public VertexConsumer uv(float u, float v) {
            return this.delegate.uv(u, v);
        }

        public VertexConsumer overlayCoords(int u, int v) {
            return this.delegate.overlayCoords(u, v);
        }

        public VertexConsumer uv2(int u, int v) {
            return this.delegate.uv2(u, v);
        }

        public VertexConsumer normal(float x, float y, float z) {
            return this.delegate.normal(x, y, z);
        }

        public void endVertex() {
            this.delegate.endVertex();
        }

        public void defaultColor(int red, int green, int blue, int alpha) {
            this.delegate.defaultColor(TintedVertexConsumer.scale(red, this.r), TintedVertexConsumer.scale(green, this.g), TintedVertexConsumer.scale(blue, this.b), alpha);
        }

        public void unsetDefaultColor() {
            this.delegate.unsetDefaultColor();
        }
    }

    private static class BindPointRenderType
    extends RenderType {
        private static final RenderType INSTANCE = BindPointRenderType.create((String)"hemalurgy_bind_point", (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(Cosmere.rl((String)"textures/block/metal_block.png"), false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));

        private BindPointRenderType(String name, VertexFormat fmt, VertexFormat.Mode glMode, int size, boolean doCrumbling, boolean depthSorting, Runnable onEnable, Runnable onDisable) {
            super(name, fmt, glMode, size, doCrumbling, depthSorting, onEnable, onDisable);
            throw new IllegalStateException("This class must not be instantiated");
        }
    }
}
