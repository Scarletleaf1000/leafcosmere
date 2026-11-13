/*
 * File updated ~ 8 - 10 - 2022 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.eventHandlers;

import leaf.cosmere.api.Metals;
import leaf.cosmere.api.helpers.CuriosHelper;
import leaf.cosmere.hemalurgy.common.BindPointGazeState;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.HumanoidEntities;
import leaf.cosmere.hemalurgy.common.SpikeStorage;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HemalurgyEntityEventHandler {

	private static boolean placingSpike = false;

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event)
	{
		if (placingSpike || event.getEntity().level().isClientSide())
		{
			return;
		}

		DamageSource source = event.getSource();
		if (!(source.getDirectEntity() instanceof Player attacker))
		{
			return;
		}

		LivingEntity target = event.getEntity();

		ItemStack stack = attacker.getMainHandItem();
		if (!(stack.getItem() instanceof HemalurgicSpikeItem spike))
		{
			return;
		}

		// A hemalurgic spike is not a weapon: cancel melee damage. Spike placement below
		// deals its own percentage-based damage through a separate hurt() call.
		event.setCanceled(true);

		if (!HumanoidEntities.isSpikeable(target))
		{
			return;
		}

		if (!spike.hasPierce(stack))
		{
			return;
		}

		if (attacker.getCooldowns().isOnCooldown(stack.getItem()))
		{
			return;
		}

		if (target.isInvulnerableTo(source))
		{
			return;
		}

		double pierceThreshold = target.getMaxHealth() * HemalurgyConfigs.SERVER.SPIKE_PIERCE_THRESHOLD.get() / 100.0;
		if (target.getHealth() >= pierceThreshold)
		{
			return;
		}

		String key = BindPointGazeState.getKey(attacker, target);
		if (key == null)
		{
			return;
		}

		int colon = key.indexOf(':');
		String group = key.substring(0, colon);
		int number = Integer.parseInt(key.substring(colon + 1));
		int slotIndex = number - 1;

		boolean slotEmpty;
		if (target instanceof Player)
		{
			slotEmpty = CuriosHelper.getStackInSlot(target, group, slotIndex).isEmpty();
		}
		else
		{
			slotEmpty = !SpikeStorage.has(target, key);
		}

		// Curios only registers the spike slots for players, so isStackValid always fails for mobs.
		// The spike-type check above already guarantees a valid HemalurgicSpikeItem, and the slots
		// have no item validators, so any spike fits any bind point.
		boolean slotValid = !(target instanceof Player)
				|| CuriosApi.isStackValid(new SlotContext(group, target, slotIndex, false, true), stack);

		boolean categoryValid = isValidForGroup(stack, group);

		if (!slotEmpty || !slotValid || !categoryValid)
		{
			return;
		}

		event.setCanceled(true);

		// Store the spike BEFORE dealing damage so that if the placement damage kills the
		// mob, onMobDeath finds the spike in SpikeStorage and drops it as an item.
		ItemStack copy = stack.copy();
		copy.setCount(1);
		if (target instanceof Player)
		{
			CuriosApi.getCuriosInventory(target).resolve().ifPresent(handler -> handler.setEquippedCurio(group, slotIndex, copy));
		}
		else
		{
			SpikeStorage.put(target, key, copy);
			SpikeStorage.syncToNearby(target);
		}

		placingSpike = true;
		try
		{
			target.hurt(target.level().damageSources().playerAttack(attacker),
					(float) (target.getMaxHealth() * HemalurgyConfigs.SERVER.SPIKE_PLACE_DAMAGE_PERCENT.get() / 100.0));
		}
		finally
		{
			placingSpike = false;
		}

		attacker.getCooldowns().addCooldown(stack.getItem(), HemalurgyConfigs.SERVER.SPIKE_COOLDOWN_TICKS.get());
		stack.shrink(1);

		attacker.sendSystemMessage(Component.translatable("message.hemalurgy.spike.placed", group, number));
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		if (placingSpike || event.getEntity().level().isClientSide())
		{
			return;
		}
		if (event.getHand() != InteractionHand.MAIN_HAND)
		{
			return;
		}

		Player player = event.getEntity();
		if (!(event.getTarget() instanceof LivingEntity target) || !HumanoidEntities.isSpikeable(target))
		{
			return;
		}

		String key = BindPointGazeState.getKey(player, target);
		if (key == null)
		{
			return;
		}
		int colon = key.indexOf(':');
		String group = key.substring(0, colon);
		int number = Integer.parseInt(key.substring(colon + 1));
		int slotIndex = number - 1;

		ItemStack stored;
		if (target instanceof Player)
		{
			stored = CuriosHelper.getStackInSlot(target, group, slotIndex);
		}
		else
		{
			stored = SpikeStorage.get(target, key);
		}
		if (stored.isEmpty())
		{
			return;
		}

		double removeThreshold = target.getMaxHealth() * HemalurgyConfigs.SERVER.SPIKE_REMOVE_HEALTH_THRESHOLD.get() / 100.0;
		if (target.getHealth() >= removeThreshold)
		{
			return;
		}

		DamageSource removalSource = target.level().damageSources().playerAttack(player);
		if (target.isInvulnerableTo(removalSource))
		{
			return;
		}

		event.setCanceled(true);

		ItemStack copy = stored.copy();
		copy.setCount(1);
		if (target instanceof Player)
		{
			CuriosApi.getCuriosInventory(target).resolve().ifPresent(handler -> handler.setEquippedCurio(group, slotIndex, ItemStack.EMPTY));
		}
		else
		{
			SpikeStorage.put(target, key, ItemStack.EMPTY);
			SpikeStorage.syncToNearby(target);
		}
		if (player.getMainHandItem().isEmpty())
		{
			player.setItemInHand(InteractionHand.MAIN_HAND, copy);
		}
		else if (!player.getInventory().add(copy))
		{
			player.drop(copy, false);
		}

		placingSpike = true;
		try
		{
			target.hurt(removalSource,
					(float) (target.getMaxHealth() * HemalurgyConfigs.SERVER.SPIKE_REMOVE_DAMAGE_PERCENT.get() / 100.0));
		}
		finally
		{
			placingSpike = false;
		}

		player.getCooldowns().addCooldown(stored.getItem(), HemalurgyConfigs.SERVER.SPIKE_COOLDOWN_TICKS.get());

		player.sendSystemMessage(Component.translatable("message.hemalurgy.spike.removed", group, number));
	}

	/** A spike's metal category must match the bind-point group it is placed in. */
	private static boolean isValidForGroup(ItemStack stack, String group)
	{
		if (!(stack.getItem() instanceof HemalurgicSpikeItem spike))
		{
			return false;
		}
		Metals.MetalType metal = spike.getMetalType();
		return switch (group)
		{
			case "physical" -> metal.isPhysicalSpike();
			case "mental" -> metal.isMentalSpike();
			case "spiritual" -> metal.isSpiritualSpike();
			case "temporal" -> metal.isTemporalSpike();
			default -> true;
		};
	}

	@SubscribeEvent
	public static void onMobDeath(LivingDeathEvent event)
	{
		LivingEntity entity = event.getEntity();
		if (entity.level().isClientSide() || entity instanceof Player)
		{
			return;
		}
		for (String key : SpikeStorage.keys(entity))
		{
			ItemStack stack = SpikeStorage.get(entity, key);
			if (!stack.isEmpty())
			{
				entity.spawnAtLocation(stack);
			}
		}
	}

}
