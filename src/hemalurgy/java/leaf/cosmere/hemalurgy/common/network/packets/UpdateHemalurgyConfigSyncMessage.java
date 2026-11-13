package leaf.cosmere.hemalurgy.common.network.packets;

import leaf.cosmere.api.Metals;
import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.config.HemalurgyServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Server-to-client sync of applied Hemalurgy config values. Separate class from
 * {@link UpdateHemalurgyConfigMessage} because Forge does not reliably handle the same
 * packet class registered in both directions.
 */
public class UpdateHemalurgyConfigSyncMessage implements ICosmerePacket
{
	private static final Map<String, Function<Double, Double>> CONFIG_APPLIERS = createAppliers();

	private final Map<String, Double> values;

	public UpdateHemalurgyConfigSyncMessage(Map<String, Double> values)
	{
		this.values = values;
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeVarInt(this.values.size());
		this.values.forEach((name, value) ->
		{
			buf.writeUtf(name);
			buf.writeDouble(value);
		});
	}

	public static UpdateHemalurgyConfigSyncMessage decode(FriendlyByteBuf buf)
	{
		int size = buf.readVarInt();
		Map<String, Double> values = new LinkedHashMap<>(size);
		for (int i = 0; i < size; i++)
		{
			values.put(buf.readUtf(), buf.readDouble());
		}
		return new UpdateHemalurgyConfigSyncMessage(values);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
		{
			this.values.forEach((name, value) ->
			{
				Function<Double, Double> applier = CONFIG_APPLIERS.get(name);
				if (applier != null)
				{
					applier.apply(value);
				}
			});
		}
	}

	private static Map<String, Function<Double, Double>> createAppliers()
	{
		HemalurgyServerConfig config = HemalurgyConfigs.SERVER;
		Map<String, Function<Double, Double>> map = new LinkedHashMap<>();

		map.put("integrityCheckTick", v -> { double c = clamp(v, 1, 1234567890); config.SPIRITWEB_INTEGRITY_TICK_CHECK.set((int) c); return c; });
		map.put("linchpinSpikeSpiritwebBonus", v -> { double c = clamp(v, 0, 100); config.LINCHPIN_SPIKE_SPIRITWEB_BONUS.set((int) c); return c; });
		map.put("allomanticPewterSpiritwebBonus", v -> { double c = clamp(v, 0, 100); config.ALLOMANTIC_PEWTER_SPIRITWEB_BONUS.set((int) c); return c; });
		map.put("feruchemicalGoldSpiritwebBonus", v -> { double c = clamp(v, 0, 100); config.FERUCHEMICAL_GOLD_SPIRITWEB_BONUS.set((int) c); return c; });
		map.put("spikePierceThreshold", v -> { double c = clamp(v, 0, 100); config.SPIKE_PIERCE_THRESHOLD.set(c); return c; });
		map.put("spikeCooldownTicks", v -> { double c = clamp(v, 0, Integer.MAX_VALUE); config.SPIKE_COOLDOWN_TICKS.set((int) c); return c; });
		map.put("spikeDamage", v -> { double c = clamp(v, 0, Double.MAX_VALUE); config.SPIKE_DAMAGE.set(c); return c; });
		map.put("maxSpikesPerEntity", v -> { double c = clamp(v, 1, 100); config.MAX_SPIKES_PER_ENTITY.set((int) c); return c; });
		map.put("spikePlaceDamagePercent", v -> { double c = clamp(v, 0, 100); config.SPIKE_PLACE_DAMAGE_PERCENT.set(c); return c; });
		map.put("spikeRemoveHealthThreshold", v -> { double c = clamp(v, 0, 100); config.SPIKE_REMOVE_HEALTH_THRESHOLD.set(c); return c; });
		map.put("spikeRemoveDamagePercent", v -> { double c = clamp(v, 0, 100); config.SPIKE_REMOVE_DAMAGE_PERCENT.set(c); return c; });
		map.put("spikeAttackDamage", v -> { double c = clamp(v, 0, Double.MAX_VALUE); config.SPIKE_ATTACK_DAMAGE.set(c); return c; });

		for (Metals.MetalType metal : HemalurgyServerConfig.SPIKE_METALS)
		{
			HemalurgyServerConfig.SpikeTuning tuning = config.SPIKE_TUNING.get(metal);
			String prefix = "spike." + metal.getName() + ".";
			map.put(prefix + "maxStrength", v -> { double c = clamp(v, 0, 1000); tuning.maxStrength().set((int) c); return c; });
			map.put(prefix + "spiritwebBonus", v -> { double c = clamp(v, -100, 100); tuning.spiritwebBonus().set((int) c); return c; });
			map.put(prefix + "stealMultiplier", v -> { double c = clamp(v, 0, 100); tuning.stealMultiplier().set(c); return c; });
		}

		return map;
	}

	private static double clamp(double value, double min, double max)
	{
		return Math.max(min, Math.min(max, value));
	}
}
