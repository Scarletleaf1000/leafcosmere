/*
 * File updated ~ 10 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.eventHandlers;

import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.HemalurgyVision;
import leaf.cosmere.hemalurgy.common.SpikeStorage;
import leaf.cosmere.hemalurgy.common.commands.HemalurgyCommands;
import leaf.cosmere.hemalurgy.common.network.packets.VisionStateMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HemalurgyCommonEvents
{
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event)
	{
		HemalurgyCommands.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (event.getEntity() instanceof ServerPlayer player)
		{
			Hemalurgy.packetHandler().sendTo(new VisionStateMessage(HemalurgyVision.isEnabled(player)), player);
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event)
	{
		if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity target && !(target instanceof Player))
		{
			SpikeStorage.syncToNearby(target);
		}
	}

}
