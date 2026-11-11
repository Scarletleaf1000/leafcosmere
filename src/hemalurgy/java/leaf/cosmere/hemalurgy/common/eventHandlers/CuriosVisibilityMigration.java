/*
 * File updated ~ 10 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.eventHandlers;

import com.mojang.logging.LogUtils;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuriosVisibilityMigration
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String MARKER_KEY = "hemalurgy_spike_slots_hidden";
	private static final List<String> SPIKE_SLOT_TYPES = List.of(
			"eyes", "linchpin", "physical", "mental", "spiritual", "temporal");

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!(event.getEntity() instanceof ServerPlayer player))
		{
			return;
		}

		try
		{
			CompoundTag persistentData = player.getPersistentData();
			if (persistentData.getBoolean(MARKER_KEY))
			{
				return;
			}

			CuriosApi.getCuriosInventory(player).ifPresent(handler -> hideSpikeSlots(handler));

			persistentData.putBoolean(MARKER_KEY, true);
			LOGGER.info("[HemalurgyCurios] hid spike slots for {}", player.getName().getString());
		}
		catch (Throwable t)
		{
			LOGGER.error("[HemalurgyCurios] failed to hide spike slots", t);
		}
	}

	private static void hideSpikeSlots(ICuriosItemHandler handler)
	{
		for (String identifier : SPIKE_SLOT_TYPES)
		{
			handler.getStacksHandler(identifier).ifPresent(stacks ->
			{
				CompoundTag tag = new CompoundTag();
				tag.putBoolean("Visible", false);
				stacks.deserializeNBT(tag);
			});
		}
	}
}
