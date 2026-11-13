/*
 * File updated ~ 12 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side store of the bind-point cube each player is currently looking at, as reported
 * by the client via {@code BindPointGazeMessage}. Entries go stale after a few ticks so a
 * missed heartbeat cannot leave a phantom gaze behind.
 */
public final class BindPointGazeState
{
	private static final int FRESHNESS_TICKS = 10;
	private static final int HEARTBEAT_TICKS = 5;

	private static final Map<UUID, Gaze> GAZE = new HashMap<>();

	private BindPointGazeState()
	{
	}

	public static int heartbeatTicks()
	{
		return HEARTBEAT_TICKS;
	}

	public static void set(Player player, int entityId, String key)
	{
		if (entityId < 0 || key == null || key.isEmpty())
		{
			GAZE.remove(player.getUUID());
		}
		else
		{
			GAZE.put(player.getUUID(), new Gaze(entityId, key, player.level().getGameTime()));
		}
	}

	/**
	 * Returns the bind-point key the attacker is gazing at on the given target, or null if the
	 * gaze is missing, stale, or aimed at a different entity.
	 */
	public static String getKey(LivingEntity attacker, LivingEntity target)
	{
		Gaze gaze = GAZE.get(attacker.getUUID());
		if (gaze == null)
		{
			return null;
		}
		if (attacker.level().getGameTime() - gaze.tick > FRESHNESS_TICKS)
		{
			return null;
		}
		if (gaze.entityId != target.getId())
		{
			return null;
		}
		return gaze.key;
	}

	public static void clear(UUID playerId)
	{
		GAZE.remove(playerId);
	}

	private record Gaze(int entityId, String key, long tick)
	{
	}
}
