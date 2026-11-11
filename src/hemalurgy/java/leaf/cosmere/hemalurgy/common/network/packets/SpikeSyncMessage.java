/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.network.packets;

import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.client.render.SpikeClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class SpikeSyncMessage implements ICosmerePacket
{
	private final int entityId;
	private final String key;
	private final ItemStack stack;

	public SpikeSyncMessage(int entityId, String key, ItemStack stack)
	{
		this.entityId = entityId;
		this.key = key;
		this.stack = stack;
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeUtf(this.key);
		buf.writeItem(this.stack);
	}

	public static SpikeSyncMessage decode(FriendlyByteBuf buf)
	{
		return new SpikeSyncMessage(buf.readInt(), buf.readUtf(), buf.readItem());
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		SpikeClientState.set(this.entityId, this.key, this.stack);
	}
}