/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import leaf.cosmere.hemalurgy.client.render.HemalurgyLayerDefinitions;
import leaf.cosmere.hemalurgy.client.render.SpikeClientState;
import leaf.cosmere.hemalurgy.client.render.model.SpikeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.Map;

/**
 * Renders spikes stored on non-player entities using the same SpikeModel that player-equipped
 * spikes use via curios, so mob spikes look identical to player spikes. Always renders
 * (independent of vision), mirroring curio rendering.
 */
public class MobSpikeLayer extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>>
{
	private final SpikeModel spikeModel;

	public MobSpikeLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderer)
	{
		super(renderer);
		ModelPart modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(HemalurgyLayerDefinitions.SPIKE);
		this.spikeModel = new SpikeModel(modelPart);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
	{
		Map<String, ItemStack> spikes = SpikeClientState.get(entity.getId());
		if (spikes == null || spikes.isEmpty())
		{
			return;
		}
		for (Map.Entry<String, ItemStack> spike : spikes.entrySet())
		{
			String[] parts = spike.getKey().split(":");
			if (parts.length != 2)
			{
				continue;
			}
			String group = parts[0];
			int number;
			try
			{
				number = Integer.parseInt(parts[1]);
			}
			catch (NumberFormatException e)
			{
				continue;
			}
			SlotContext slotContext = new SlotContext(group, entity, number - 1, false, true);
			this.spikeModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			this.spikeModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			ICurioRenderer.followBodyRotations(entity, this.spikeModel);
			this.spikeModel.render(spike.getValue(), slotContext, poseStack, buffer, packedLight);
		}
	}
}