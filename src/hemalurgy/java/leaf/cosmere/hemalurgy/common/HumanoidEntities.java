/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

/**
 * Common-side check for entities that can carry bind points: players and humanoid mobs.
 * Mirrors the set of vanilla entities rendered with a HumanoidModel.
 */
public final class HumanoidEntities
{
	private HumanoidEntities()
	{
	}

	public static boolean isSpikeable(LivingEntity entity)
	{
		return entity instanceof Player
				|| entity instanceof Zombie
				|| entity instanceof AbstractSkeleton
				|| entity instanceof AbstractIllager
				|| entity instanceof AbstractPiglin
				|| entity instanceof AbstractVillager
				|| entity instanceof Witch
				|| entity instanceof Giant;
	}
}
