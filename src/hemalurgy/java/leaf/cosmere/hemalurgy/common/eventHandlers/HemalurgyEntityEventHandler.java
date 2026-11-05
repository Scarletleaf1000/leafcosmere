/*
 * File updated ~ 8 - 10 - 2022 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.eventHandlers;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.text.TextHelper;
import leaf.cosmere.common.registration.impl.ItemRegistryObject;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.config.HemalurgyServerConfig;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HemalurgyEntityEventHandler {

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
