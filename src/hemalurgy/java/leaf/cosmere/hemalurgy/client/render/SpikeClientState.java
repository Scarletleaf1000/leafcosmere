/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.render;

import leaf.cosmere.hemalurgy.common.Hemalurgy;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side mirror of spikes placed on non-player entities, synced via SpikeSyncMessage.
 * entityId -> bind-point key -> spike stack.
 */
@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SpikeClientState
{
	private static final Map<Integer, Map<String, ItemStack>> SPIKES = new HashMap<Integer, Map<String, ItemStack>>();

	private SpikeClientState()
	{
	}

	public static void set(int entityId, String key, ItemStack stack)
	{
		Map<String, ItemStack> spikes = SPIKES.computeIfAbsent(entityId, k -> new HashMap<String, ItemStack>());
		if (stack.isEmpty())
		{
			spikes.remove(key);
			if (spikes.isEmpty())
			{
				SPIKES.remove(entityId);
			}
		}
		else
		{
			spikes.put(key, stack);
		}
	}

	public static Map<String, ItemStack> get(int entityId)
	{
		return SPIKES.get(entityId);
	}

	@SubscribeEvent
	public static void onLogout(TickEvent.ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}
		if (net.minecraft.client.Minecraft.getInstance().player == null)
		{
			SPIKES.clear();
		}
	}
}