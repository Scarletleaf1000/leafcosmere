/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.render;

import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class VisionClientState
{
	public static boolean enabled = true;

	private VisionClientState()
	{
	}

	public static boolean isActive(LocalPlayer player)
	{
		return enabled && holdsPiercedSpike(player);
	}

	private static boolean holdsPiercedSpike(LocalPlayer player)
	{
		return isPierced(player.getMainHandItem()) || isPierced(player.getOffhandItem());
	}

	private static boolean isPierced(ItemStack stack)
	{
		return stack.getItem() instanceof HemalurgicSpikeItem && HemalurgicSpikeItem.hasPierce(stack);
	}
}