/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common;

import leaf.cosmere.hemalurgy.common.network.packets.SpikeSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores spikes on non-player entities in their persistent data, under "hemalurgy_spikes",
 * with keys in BindPointTable's format ("group:number") and values from ItemStack.save(...).
 */
public final class SpikeStorage
{
	private static final String TAG = "hemalurgy_spikes";

	private SpikeStorage()
	{
	}

	public static boolean has(LivingEntity entity, String key)
	{
		return !get(entity, key).isEmpty();
	}

	public static ItemStack get(LivingEntity entity, String key)
	{
		CompoundTag spikes = entity.getPersistentData().getCompound(TAG);
		CompoundTag stackTag = spikes.getCompound(key);
		if (stackTag.isEmpty())
		{
			return ItemStack.EMPTY;
		}
		return ItemStack.of(stackTag);
	}

	public static void put(LivingEntity entity, String key, ItemStack stack)
	{
		CompoundTag spikes = entity.getPersistentData().getCompound(TAG);
		spikes.put(key, stack.save(new CompoundTag()));
		entity.getPersistentData().put(TAG, spikes);
	}

	public static List<String> keys(LivingEntity entity)
	{
		List<String> keys = new ArrayList<String>();
		CompoundTag spikes = entity.getPersistentData().getCompound(TAG);
		for (String key : spikes.getAllKeys())
		{
			keys.add(key);
		}
		return keys;
	}

	/** Sends all stored spikes for the entity to nearby players so clients can render them. */
	public static void syncToNearby(LivingEntity entity)
	{
		if (entity.level().isClientSide())
		{
			return;
		}
		for (String key : keys(entity))
		{
			Hemalurgy.packetHandler().sendToAllAround(new SpikeSyncMessage(entity.getId(), key, get(entity, key)), entity.level().dimension(), entity.blockPosition(), 64);
		}
	}
}
