/*
 * File updated ~ 8 - 10 - 2022 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.eventHandlers;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.helpers.CuriosHelper;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.text.TextHelper;
import leaf.cosmere.common.registration.impl.ItemRegistryObject;
import leaf.cosmere.hemalurgy.common.BindPointHit;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.HumanoidEntities;
import leaf.cosmere.hemalurgy.common.SpikeStorage;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.config.HemalurgyServerConfig;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
		if (!HumanoidEntities.isSpikeable(target))
		{
			return;
		}

		ItemStack stack = attacker.getMainHandItem();
		if (!(stack.getItem() instanceof HemalurgicSpikeItem spike) || !spike.hasPierce(stack))
		{
			debug("gate: hand", attacker, target, stack);
			return;
		}

		if (attacker.getCooldowns().isOnCooldown(stack.getItem()))
		{
			debug("gate: cooldown", attacker, target, stack);
			return;
		}

		if (target.isInvulnerableTo(source))
		{
			debug("gate: invulnerable", attacker, target, stack);
			return;
		}

		String key = BindPointHit.findHit(target, attacker.getEyePosition(1.0f), attacker.getLookAngle(), 1.0f);
		if (key == null)
		{
			debug("gate: no hit", attacker, target, stack);
			return;
		}
		debug("hit: " + key, attacker, target, stack);

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
			debug("gate: slot empty=" + slotEmpty + " valid=" + slotValid + " category=" + categoryValid + " key=" + key, attacker, target, stack);
			return;
		}
		debug("placing " + key, attacker, target, stack);

		event.setCanceled(true);

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
		stack.shrink(1);

		attacker.getCooldowns().addCooldown(stack.getItem(), HemalurgyConfigs.SERVER.SPIKE_COOLDOWN_TICKS.get());

		attacker.sendSystemMessage(Component.translatable("message.hemalurgy.spike.placed", group, number));
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

	private static void debug(String reason, Player attacker, LivingEntity target, ItemStack stack)
	{
		CosmereAPI.logger.info("[SPIKE-DEBUG] {} attacker={} target={} stack={} pierce={}", reason, attacker.getName().getString(), target.getName().getString(), stack.getItem(), stack.getItem() instanceof HemalurgicSpikeItem s && s.hasPierce(stack));
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

	@SubscribeEvent
	public static void onEntityDamage(LivingHurtEvent event) {
		LivingEntity target = event.getEntity();
		if (!(event.getSource().getEntity() instanceof Player p)) return;

		ItemStack stack = p.getMainHandItem();
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof HemalurgicSpikeItem spike) {
				if (!spike.hasPierce(stack)) return;
				Float threshold = HemalurgyConfigs.SERVER.SPIKE_PIERCE_THRESHOLD.get().floatValue();
				if (target.getHealth() > threshold) return;
				if (p.getCooldowns().isOnCooldown(stack.getItem())) return;
				addCooldownToAllSpikes(p);
			}
		}
	}

	private static void addCooldownToAllSpikes(Player p) {
		int cooldownTicks = HemalurgyConfigs.SERVER.SPIKE_COOLDOWN_TICKS.get();
		for (ItemRegistryObject<HemalurgicSpikeItem> spike : HemalurgyItems.METAL_SPIKE.values()) {
			p.getCooldowns().addCooldown(spike.asItem(), cooldownTicks);
		}
	}

}
