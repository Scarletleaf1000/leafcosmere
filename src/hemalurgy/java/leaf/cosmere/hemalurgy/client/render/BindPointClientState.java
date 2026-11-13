/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.render;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side cache of rendered bind-point cube world positions, captured each frame
 * by {@code BindPointLayer}. Used for accurate client-side ray-casting on click.
 */
public final class BindPointClientState
{
	private static final Map<Integer, Map<String, Vec3>> CUBE_POSITIONS = new HashMap<>();

	private BindPointClientState()
	{
	}

	public static void put(int entityId, String key, Vec3 pos)
	{
		CUBE_POSITIONS.computeIfAbsent(entityId, k -> new HashMap<>()).put(key, pos);
	}

	public static Map<String, Vec3> get(int entityId)
	{
		return CUBE_POSITIONS.get(entityId);
	}

	public static void remove(int entityId)
	{
		CUBE_POSITIONS.remove(entityId);
	}

	public static java.util.Set<Integer> entityIds()
	{
		return new java.util.HashSet<>(CUBE_POSITIONS.keySet());
	}

	public static void clear()
	{
		CUBE_POSITIONS.clear();
	}

	/**
	 * Finds the bind-point cube whose world position is closest to the ray from eye along look,
	 * within maxDistance blocks and hitRadius of the cube centre.
	 */
	public static Hit findHit(int entityId, Vec3 eye, Vec3 look, double maxDistance, double hitRadius)
	{
		Map<String, Vec3> cubes = CUBE_POSITIONS.get(entityId);
		if (cubes == null || cubes.isEmpty())
		{
			return null;
		}
		Hit best = null;
		double bestT = Double.MAX_VALUE;
		for (Map.Entry<String, Vec3> entry : cubes.entrySet())
		{
			Vec3 centre = entry.getValue();
			Vec3 toCentre = centre.subtract(eye);
			double t = toCentre.dot(look);
			if (t <= 0.0 || t > maxDistance)
			{
				continue;
			}
			Vec3 closest = eye.add(look.scale(t));
			double dist = closest.distanceTo(centre);
			if (dist <= hitRadius && t < bestT)
			{
				best = new Hit(entry.getKey(), t, dist);
				bestT = t;
			}
		}
		return best;
	}

	public record Hit(String key, double t, double distance)
	{
	}
}
