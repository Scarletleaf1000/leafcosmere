/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bind-point cube positions and body-part pivots, shared between the client renderer
 * ({@code BindPointTable}/{@code BindPointLayer}) and the common hit test
 * ({@code BindPointHit}). Values are identical to BindPointTable's table.
 */
public final class BindPointGeometry
{
	private static final Map<String, CubeDef> CUBE_TABLE = BindPointGeometry.buildCubeTable();
	private static final Map<String, float[]> PART_PIVOTS = Map.of(
			"head", new float[]{0.0f, 0.0f, 0.0f},
			"body", new float[]{0.0f, 0.0f, 0.0f},
			"leftArm", new float[]{5.0f, 2.0f, 0.0f},
			"rightArm", new float[]{-5.0f, 2.0f, 0.0f},
			"leftLeg", new float[]{1.9f, 12.0f, 0.0f},
			"rightLeg", new float[]{-1.9f, 12.0f, 0.0f});

	private BindPointGeometry()
	{
	}

	public static Set<String> keys()
	{
		return CUBE_TABLE.keySet();
	}

	public static CubeDef cube(String key)
	{
		return CUBE_TABLE.get(key);
	}

	public static float[] pivot(String group)
	{
		return PART_PIVOTS.get(group);
	}

	private static Map<String, CubeDef> buildCubeTable()
	{
		HashMap<String, CubeDef> table = new HashMap<>();
		table.put("eyes:1", new CubeDef("head", 2.0f, -2.0f, -4.0f));
		table.put("eyes:2", new CubeDef("head", -2.0f, -2.0f, -4.0f));
		table.put("linchpin:1", new CubeDef("body", 0.0f, 1.25f, 2.0f));
		table.put("mental:1", new CubeDef("body", 0.0f, 3.75f, 2.0f));
		table.put("mental:2", new CubeDef("body", 0.0f, 5.75f, 2.0f));
		table.put("mental:3", new CubeDef("body", 0.0f, 7.75f, 2.0f));
		table.put("mental:4", new CubeDef("body", 0.0f, 9.75f, 2.0f));
		table.put("mental:5", new CubeDef("body", 2.25f, 6.25f, -2.0f));
		table.put("mental:6", new CubeDef("body", -2.25f, 6.25f, -2.0f));
		table.put("mental:7", new CubeDef("leftArm", 2.0f, 2.25f, 0.0f));
		table.put("mental:8", new CubeDef("rightArm", -2.0f, 2.25f, 0.0f));
		table.put("mental:9", new CubeDef("leftLeg", 0.0f, 4.25f, -2.0f));
		table.put("mental:10", new CubeDef("rightLeg", 0.0f, 4.25f, -2.0f));
		table.put("physical:1", new CubeDef("body", 0.0f, 5.75f, -2.0f));
		table.put("physical:2", new CubeDef("body", 0.0f, 9.75f, -2.0f));
		table.put("physical:3", new CubeDef("body", 2.25f, 4.25f, -2.0f));
		table.put("physical:4", new CubeDef("body", -2.25f, 4.25f, -2.0f));
		table.put("physical:5", new CubeDef("leftArm", 1.0f, -2.0f, 0.0f));
		table.put("physical:6", new CubeDef("rightArm", -1.0f, -2.0f, 0.0f));
		table.put("physical:7", new CubeDef("leftLeg", 0.0f, 2.25f, -2.0f));
		table.put("physical:8", new CubeDef("rightLeg", 0.0f, 2.25f, -2.0f));
		table.put("spiritual:1", new CubeDef("body", 2.25f, 10.25f, -2.0f));
		table.put("spiritual:2", new CubeDef("body", -2.25f, 10.25f, -2.0f));
		table.put("spiritual:3", new CubeDef("leftArm", 2.0f, 0.25f, 0.0f));
		table.put("spiritual:4", new CubeDef("rightArm", -2.0f, 0.25f, 0.0f));
		table.put("temporal:1", new CubeDef("body", 2.25f, 8.25f, -2.0f));
		table.put("temporal:2", new CubeDef("body", -2.25f, 8.25f, -2.0f));
		table.put("temporal:3", new CubeDef("leftArm", 1.0f, 0.25f, 2.5f));
		table.put("temporal:4", new CubeDef("rightArm", -1.0f, 0.25f, 2.5f));
		table.put("temporal:5", new CubeDef("leftArm", 1.0f, 2.25f, 2.5f));
		table.put("temporal:6", new CubeDef("rightArm", -1.0f, 2.25f, 2.5f));
		table.put("temporal:7", new CubeDef("leftLeg", 2.5f, 4.25f, -1.0f));
		table.put("temporal:8", new CubeDef("rightLeg", -2.5f, 4.25f, -1.0f));
		table.put("temporal:9", new CubeDef("leftArm", 2.5f, 8.25f, 0.0f));
		table.put("temporal:10", new CubeDef("rightArm", -2.5f, 8.25f, 0.0f));
		table.put("temporal:11", new CubeDef("leftArm", 2.5f, 6.25f, 0.0f));
		table.put("temporal:12", new CubeDef("rightArm", -2.5f, 6.25f, 0.0f));
		table.put("temporal:13", new CubeDef("leftLeg", 0.0f, 6.25f, -2.0f));
		table.put("temporal:14", new CubeDef("rightLeg", 0.0f, 6.25f, -2.0f));
		table.put("temporal:15", new CubeDef("leftLeg", 0.0f, 8.25f, -2.0f));
		table.put("temporal:16", new CubeDef("rightLeg", 0.0f, 8.25f, -2.0f));
		return table;
	}

	public record CubeDef(String group, float x, float y, float z)
	{
	}
}
