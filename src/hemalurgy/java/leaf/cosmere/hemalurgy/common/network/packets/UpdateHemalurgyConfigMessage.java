package leaf.cosmere.hemalurgy.common.network.packets;

import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.config.HemalurgyServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class UpdateHemalurgyConfigMessage implements ICosmerePacket
{
	private static final Map<String, Function<Double, Double>> CONFIG_APPLIERS = createAppliers();

	private final Map<String, Double> values;

	public UpdateHemalurgyConfigMessage(Map<String, Double> values)
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

	public static UpdateHemalurgyConfigMessage decode(FriendlyByteBuf buf)
	{
		int size = buf.readVarInt();
		Map<String, Double> values = new LinkedHashMap<>(size);
		for (int i = 0; i < size; i++)
		{
			values.put(buf.readUtf(), buf.readDouble());
		}
		return new UpdateHemalurgyConfigMessage(values);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER)
		{
			ServerPlayer player = context.getSender();
			if (player == null)
			{
				return;
			}

			if (!player.hasPermissions(2))
			{
				player.sendSystemMessage(Component.translatable("message.hemalurgy.config.denied"));
				return;
			}

			Map<String, Double> applied = new LinkedHashMap<>();
			this.values.forEach((name, value) ->
			{
				Function<Double, Double> applier = CONFIG_APPLIERS.get(name);
				if (applier != null)
				{
					applied.put(name, applier.apply(value));
				}
			});

			HemalurgyConfigs.SERVER.save();
			Hemalurgy.packetHandler().sendTo(new UpdateHemalurgyConfigMessage(applied), player);
			player.sendSystemMessage(Component.translatable("message.hemalurgy.config.updated"));
		}
		else
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
		map.put("lerasatiumMaxSpikeStrength", v -> { double c = clamp(v, 1, 20); config.LERASATIUM_MAX_SPIKE_STRENGTH.set((int) c); return c; });
		map.put("chromiumMaxSpikeStrength", v -> { double c = clamp(v, 1, 5); config.CHROMIUM_MAX_SPIKE_STRENGTH.set((int) c); return c; });
		map.put("powerMaxSpikeStrength", v -> { double c = clamp(v, 1, 20); config.DEFAULT_POWER_MAX_SPIKE_STRENGTH.set((int) c); return c; });
		map.put("linchpinSpikeSpiritwebBonus", v -> { double c = clamp(v, 0, 100); config.LINCHPIN_SPIKE_SPIRITWEB_BONUS.set((int) c); return c; });
		map.put("allomanticPewterSpiritwebBonus", v -> { double c = clamp(v, 0, 100); config.ALLOMANTIC_PEWTER_SPIRITWEB_BONUS.set((int) c); return c; });
		map.put("feruchemicalGoldSpiritwebBonus", v -> { double c = clamp(v, 0, 100); config.FERUCHEMICAL_GOLD_SPIRITWEB_BONUS.set((int) c); return c; });
		map.put("ironSpikeStrengthMultiplier", v -> { double c = clamp(v, 0, 100); config.IRON_SPIKE_STRENGTH_MULTIPLIER.set(c); return c; });
		map.put("spikePierceThreshold", v -> { double c = clamp(v, 0.01, Double.MAX_VALUE); config.SPIKE_PIERCE_THRESHOLD.set(c); return c; });
		map.put("spikeCooldownTicks", v -> { double c = clamp(v, 0, Integer.MAX_VALUE); config.SPIKE_COOLDOWN_TICKS.set((int) c); return c; });
		map.put("spikeDamage", v -> { double c = clamp(v, 0, Double.MAX_VALUE); config.SPIKE_DAMAGE.set(c); return c; });
		map.put("maxSpikesPerEntity", v -> { double c = clamp(v, 1, 100); config.MAX_SPIKES_PER_ENTITY.set((int) c); return c; });

		return map;
	}

	private static double clamp(double value, double min, double max)
	{
		return Math.max(min, Math.min(max, value));
	}
}
