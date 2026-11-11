package leaf.cosmere.hemalurgy.common.network;

import leaf.cosmere.common.Cosmere;
import leaf.cosmere.common.network.BasePacketHandler;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.network.packets.PlaceSpikeMessage;
import leaf.cosmere.hemalurgy.common.network.packets.SpikeSyncMessage;
import leaf.cosmere.hemalurgy.common.network.packets.UnequipSpikeMessage;
import leaf.cosmere.hemalurgy.common.network.packets.UpdateHemalurgyConfigMessage;
import leaf.cosmere.hemalurgy.common.network.packets.VisionStateMessage;
import net.minecraftforge.network.simple.SimpleChannel;

public class HemalurgyPacketHandler extends BasePacketHandler
{
	private final SimpleChannel NETWORK_CHANNEL = createChannel(Cosmere.rl(Hemalurgy.MODID), Hemalurgy.instance.versionNumber);

	@Override
	protected SimpleChannel getChannel()
	{
		return NETWORK_CHANNEL;
	}

	@Override
	public void initialize()
	{
		registerClientToServer(PlaceSpikeMessage.class, PlaceSpikeMessage::decode);
		registerClientToServer(UnequipSpikeMessage.class, UnequipSpikeMessage::decode);
		registerClientToServer(UpdateHemalurgyConfigMessage.class, UpdateHemalurgyConfigMessage::decode);
		registerServerToClient(UpdateHemalurgyConfigMessage.class, UpdateHemalurgyConfigMessage::decode);
		registerServerToClient(VisionStateMessage.class, VisionStateMessage::decode);
		registerServerToClient(SpikeSyncMessage.class, SpikeSyncMessage::decode);
	}
}
