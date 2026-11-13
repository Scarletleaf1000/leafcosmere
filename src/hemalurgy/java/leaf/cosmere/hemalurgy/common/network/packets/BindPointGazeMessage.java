/*
 * File updated ~ 12 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.network.packets;

import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.common.BindPointGazeState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

/**
 * Client -> Server: reports which bind-point cube the local player is currently looking at.
 * entityId -1 (or empty key) means no bind point is hovered.
 */
public class BindPointGazeMessage implements ICosmerePacket
{
	private final int entityId;
	private final String key;

	public BindPointGazeMessage(int entityId, String key)
	{
		this.entityId = entityId;
		this.key = key;
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeVarInt(this.entityId);
		buf.writeUtf(this.key == null ? "" : this.key);
	}

	public static BindPointGazeMessage decode(FriendlyByteBuf buf)
	{
		return new BindPointGazeMessage(buf.readVarInt(), buf.readUtf());
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		ServerPlayer player = context.getSender();
		if (player != null)
		{
			BindPointGazeState.set(player, this.entityId, this.key);
		}
	}
}
