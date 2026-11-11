/*
 * File updated ~ 10 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.network.packets;

import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.client.render.VisionClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class VisionStateMessage implements ICosmerePacket
{
	private final boolean enabled;

	public VisionStateMessage(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBoolean(this.enabled);
	}

	public static VisionStateMessage decode(FriendlyByteBuf buf)
	{
		return new VisionStateMessage(buf.readBoolean());
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		VisionClientState.enabled = this.enabled;
	}
}
