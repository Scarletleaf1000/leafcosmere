/*
 * File updated ~ 12 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.events;

import leaf.cosmere.hemalurgy.client.render.BindPointClientState;
import leaf.cosmere.hemalurgy.common.BindPointGazeState;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.network.packets.BindPointGazeMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

/**
 * Each client tick, ray-casts from the camera through the bind-point cube world positions
 * captured by {@code BindPointLayer} and reports the hovered cube to the server. Sent on
 * change plus a periodic heartbeat so the server-side gaze never goes stale mid-aim.
 */
@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BindPointClientEvents
{
	private static final double MAX_DISTANCE = 8.0;
	private static final double HIT_RADIUS = 0.08;

	private static int lastSentTick = -1;
	private static int lastSentEntity = -1;
	private static String lastSentKey = "";

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null)
		{
			lastSentTick = -1;
			lastSentEntity = -1;
			lastSentKey = "";
			BindPointClientState.clear();
			return;
		}

		for (int id : BindPointClientState.entityIds())
		{
			if (mc.level.getEntity(id) == null)
			{
				BindPointClientState.remove(id);
			}
		}

		int entityId = -1;
		String key = null;
		if (mc.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity)
		{
			entityId = hit.getEntity().getId();
			float partial = mc.getFrameTime();
			Vec3 eye = mc.gameRenderer.getMainCamera().getPosition();
			Vec3 look = mc.player.getViewVector(partial);
			BindPointClientState.Hit h = BindPointClientState.findHit(entityId, eye, look, MAX_DISTANCE, HIT_RADIUS);
			if (h != null)
			{
				key = h.key();
			}
		}

		int msgEntity = key == null ? -1 : entityId;
		String msgKey = key == null ? "" : key;
		long tick = mc.level.getGameTime();
		boolean changed = msgEntity != lastSentEntity || !Objects.equals(msgKey, lastSentKey);
		boolean heartbeat = !msgKey.isEmpty() && tick - lastSentTick >= BindPointGazeState.heartbeatTicks();
		if (changed || heartbeat)
		{
			Hemalurgy.packetHandler().sendToServer(new BindPointGazeMessage(msgEntity, msgKey));
			lastSentTick = (int) tick;
			lastSentEntity = msgEntity;
			lastSentKey = msgKey;
		}
	}
}
