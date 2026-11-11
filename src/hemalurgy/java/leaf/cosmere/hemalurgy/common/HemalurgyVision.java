/*
 * File updated ~ 10 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class HemalurgyVision
{
	public static final String KEY = "hemalurgy_vision";

	private HemalurgyVision()
	{
	}

	public static boolean isEnabled(ServerPlayer player)
	{
		CompoundTag data = player.getPersistentData();
		return !data.contains(KEY) || data.getBoolean(KEY);
	}

	public static void setEnabled(ServerPlayer player, boolean enabled)
	{
		player.getPersistentData().putBoolean(KEY, enabled);
	}
}
