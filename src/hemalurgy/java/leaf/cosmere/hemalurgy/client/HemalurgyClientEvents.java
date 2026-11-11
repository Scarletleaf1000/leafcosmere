/*
 * File updated ~ 19 - 7 - 2023 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client;

import leaf.cosmere.hemalurgy.client.render.layer.BindPointLayer;
import leaf.cosmere.hemalurgy.client.render.layer.MobSpikeLayer;
import leaf.cosmere.hemalurgy.client.render.renderer.KolossLargeRenderer;
import leaf.cosmere.hemalurgy.client.render.renderer.KolossMediumRenderer;
import leaf.cosmere.hemalurgy.client.render.renderer.KolossSmallRenderer;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HemalurgyClientEvents
{
	@SubscribeEvent
	public static void RegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		//I don't think this actually works for this?
		//see the other reference to koloss renderer constructor
		event.registerEntityRenderer(HemalurgyEntityTypes.KOLOSS_LARGE.get(), KolossLargeRenderer::new);
		event.registerEntityRenderer(HemalurgyEntityTypes.KOLOSS_MEDIUM.get(), KolossMediumRenderer::new);
		event.registerEntityRenderer(HemalurgyEntityTypes.KOLOSS_SMALL.get(), KolossSmallRenderer::new);
	}

	@SubscribeEvent
	public static void RegisterLayers(EntityRenderersEvent.AddLayers event)
	{
		for (String skin : event.getSkins())
		{
			PlayerRenderer renderer = event.getSkin(skin);
			if (renderer != null)
			{
				addBindPointLayer(renderer);
			}
		}
	}

	@SubscribeEvent
	public static void RegisterMobLayers(EntityRenderersEvent.AddLayers event)
	{
		for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : Minecraft.getInstance().getEntityRenderDispatcher().renderers.entrySet())
		{
			EntityType<?> type = entry.getKey();
			if (type == EntityType.PLAYER)
			{
				continue;
			}

			EntityRenderer<?> renderer = entry.getValue();
			if (renderer instanceof LivingEntityRenderer && !(renderer instanceof PlayerRenderer))
			{
				LivingEntityRenderer<?, ?> livingRenderer = (LivingEntityRenderer<?, ?>) renderer;
				if (livingRenderer.getModel() instanceof HumanoidModel)
				{
					addBindPointLayer(livingRenderer);
				}
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void addBindPointLayer(LivingEntityRenderer<?, ?> renderer)
	{
		renderer.addLayer((RenderLayer) new BindPointLayer((RenderLayerParent) renderer));
		renderer.addLayer((RenderLayer) new MobSpikeLayer((RenderLayerParent) renderer));
	}
}
