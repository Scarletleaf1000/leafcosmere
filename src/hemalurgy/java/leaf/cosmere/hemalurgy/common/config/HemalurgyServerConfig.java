/*
 * File updated ~ 7 - 6 - 2023 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.config;

import leaf.cosmere.api.EnumUtils;
import leaf.cosmere.api.Metals;
import leaf.cosmere.common.config.ICosmereConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class HemalurgyServerConfig implements ICosmereConfig
{

	/** Metals that have a hemalurgic spike item. */
	public static final List<Metals.MetalType> SPIKE_METALS = Arrays.stream(EnumUtils.METAL_TYPES)
			.filter(Metals.MetalType::hasHemalurgicEffect)
			.toList();

	private final ForgeConfigSpec configSpec;

	// Boost amount for Duralumin and Nicrosil
	public final ForgeConfigSpec.IntValue SPIRITWEB_INTEGRITY_TICK_CHECK;
	public final ForgeConfigSpec.IntValue LINCHPIN_SPIKE_SPIRITWEB_BONUS;
	public final ForgeConfigSpec.IntValue ALLOMANTIC_PEWTER_SPIRITWEB_BONUS;
	public final ForgeConfigSpec.IntValue FERUCHEMICAL_GOLD_SPIRITWEB_BONUS;
	public final ForgeConfigSpec.DoubleValue SPIKE_PIERCE_THRESHOLD;
	public final ForgeConfigSpec.IntValue SPIKE_COOLDOWN_TICKS;
	public final ForgeConfigSpec.DoubleValue SPIKE_DAMAGE;
	public final ForgeConfigSpec.IntValue MAX_SPIKES_PER_ENTITY;
	public final ForgeConfigSpec.DoubleValue SPIKE_PLACE_DAMAGE_PERCENT;
	public final ForgeConfigSpec.DoubleValue SPIKE_REMOVE_HEALTH_THRESHOLD;
	public final ForgeConfigSpec.DoubleValue SPIKE_REMOVE_DAMAGE_PERCENT;
	public final ForgeConfigSpec.DoubleValue SPIKE_ATTACK_DAMAGE;

	/** Per-metal spike tuning, keyed by metal type. */
	public final Map<Metals.MetalType, SpikeTuning> SPIKE_TUNING = new EnumMap<>(Metals.MetalType.class);

	public record SpikeTuning(
			ForgeConfigSpec.IntValue maxStrength,
			ForgeConfigSpec.IntValue spiritwebBonus,
			ForgeConfigSpec.DoubleValue stealMultiplier)
	{
	}


	HemalurgyServerConfig()
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("Hemalurgy Config. This config is synced between server and client.").push("hemalurgy");

		SPIRITWEB_INTEGRITY_TICK_CHECK = builder.comment("What tick count should be used to check spiritweb integrity? There are 20 ticks in a second.").defineInRange("integrityCheckTick", 20, 1, 1234567890);

		LINCHPIN_SPIKE_SPIRITWEB_BONUS = builder.comment("What bonus does a linchpin spike grant spiritweb integrity.").defineInRange("linchpinSpikeSpiritwebBonus", 3, 0, 100);
		ALLOMANTIC_PEWTER_SPIRITWEB_BONUS = builder.comment("What bonus does Allomantic pewter grant spiritweb integrity.").defineInRange("allomanticPewterSpiritwebBonus", 3, 0, 100);
		FERUCHEMICAL_GOLD_SPIRITWEB_BONUS = builder.comment("What bonus does Feruchemical gold grant spiritweb integrity.").defineInRange("feruchemicalGoldSpiritwebBonus", 6, 0, 100);
		SPIKE_PIERCE_THRESHOLD = builder.comment("The percentage of max health a entity has to be under to pierce them with a spike.").defineInRange("spikePierceThreshold", 50.0, 0.0, 100.0);
		SPIKE_COOLDOWN_TICKS = builder.comment("The amount of ticks a spike should be on cooldown after piercing.").defineInRange("spikeCooldownTicks", 1200, 0, Integer.MAX_VALUE);
		SPIKE_DAMAGE = builder.comment("The amount of damage dealt when placing a spike on an entity.").defineInRange("spikeDamage", 5.0, 0.0, Double.MAX_VALUE);
		MAX_SPIKES_PER_ENTITY = builder.comment("The maximum number of spikes that can be placed on a single entity.").defineInRange("maxSpikesPerEntity", 10, 1, 100);
		SPIKE_PLACE_DAMAGE_PERCENT = builder.comment("Percentage of the target's max health dealt when a spike is placed.").defineInRange("spikePlaceDamagePercent", 10.0, 0.0, 100.0);
		SPIKE_REMOVE_HEALTH_THRESHOLD = builder.comment("The target's health must be below this percentage of max health to remove a spike by right-clicking.").defineInRange("spikeRemoveHealthThreshold", 50.0, 0.0, 100.0);
		SPIKE_REMOVE_DAMAGE_PERCENT = builder.comment("Percentage of the target's max health dealt when a spike is removed.").defineInRange("spikeRemoveDamagePercent", 10.0, 0.0, 100.0);
		SPIKE_ATTACK_DAMAGE = builder.comment("Base attack damage modifier for spike items when held.").defineInRange("spikeAttackDamage", 0.0, 0.0, Double.MAX_VALUE);

		for (Metals.MetalType metal : SPIKE_METALS)
		{
			builder.push(metal.getName());
			int defaultMaxStrength = defaultMaxSpikeStrength(metal);
			double defaultSteal = defaultStealMultiplier(metal);
			ForgeConfigSpec.IntValue maxStrength = builder.comment("Max strength this spike can hold. 0 = uncapped.").defineInRange("maxSpikeStrength", defaultMaxStrength, 0, 1000);
			ForgeConfigSpec.IntValue spiritwebBonus = builder.comment("Spiritweb integrity bonus when this spike is equipped.").defineInRange("spiritwebBonus", 0, -100, 100);
			ForgeConfigSpec.DoubleValue stealMultiplier = builder.comment("Multiplier on how much power/attribute this spike steals.").defineInRange("stealMultiplier", defaultSteal, 0.0, 100.0);
			builder.pop();
			SPIKE_TUNING.put(metal, new SpikeTuning(maxStrength, spiritwebBonus, stealMultiplier));
		}

		builder.pop();
		configSpec = builder.build();
	}

	private static double defaultStealMultiplier(Metals.MetalType metal)
	{
		return switch (metal)
		{
			case LERASATIUM -> 0.5;
			//manifestation-stealing metals historically stole at 70%
			case STEEL, PEWTER, BRASS, BRONZE, CADMIUM, BENDALLOY, GOLD, ELECTRUM, ATIUM, IRON -> 0.7;
			default -> 1.0;
		};
	}

	private static int defaultMaxSpikeStrength(Metals.MetalType metal)
	{
		return switch (metal)
		{
			case LERASATIUM -> 5;
			case CHROMIUM -> 3;
			//attribute spikes were previously uncapped
			case IRON, TIN, COPPER, ZINC, ALUMINUM, DURALUMIN, NICROSIL -> 0;
			default -> 7;
		};
	}

	@Override
	public String getFileName()
	{
		return "HemalurgyServer";
	}

	@Override
	public ForgeConfigSpec getConfigSpec()
	{
		return configSpec;
	}

	@Override
	public Type getConfigType()
	{
		return Type.SERVER;
	}

	@Override
	public void clearCache()
	{
		SPIRITWEB_INTEGRITY_TICK_CHECK.clearCache();
		LINCHPIN_SPIKE_SPIRITWEB_BONUS.clearCache();
		ALLOMANTIC_PEWTER_SPIRITWEB_BONUS.clearCache();
		FERUCHEMICAL_GOLD_SPIRITWEB_BONUS.clearCache();
		SPIKE_PIERCE_THRESHOLD.clearCache();
		SPIKE_COOLDOWN_TICKS.clearCache();
		SPIKE_DAMAGE.clearCache();
		MAX_SPIKES_PER_ENTITY.clearCache();
		SPIKE_PLACE_DAMAGE_PERCENT.clearCache();
		SPIKE_REMOVE_HEALTH_THRESHOLD.clearCache();
		SPIKE_REMOVE_DAMAGE_PERCENT.clearCache();
		SPIKE_ATTACK_DAMAGE.clearCache();
		SPIKE_TUNING.values().forEach(t ->
		{
			t.maxStrength().clearCache();
			t.spiritwebBonus().clearCache();
			t.stealMultiplier().clearCache();
		});
	}
}
