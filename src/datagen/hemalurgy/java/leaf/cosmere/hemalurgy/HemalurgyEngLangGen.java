/*
 * File updated ~ 20 - 11 - 2024 ~ Leaf
 */

package leaf.cosmere.hemalurgy;

import leaf.cosmere.api.Constants;
import leaf.cosmere.api.EnumUtils;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.helpers.RegistryHelper;
import leaf.cosmere.api.providers.IEntityTypeProvider;
import leaf.cosmere.api.text.StringHelper;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyAttributes;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyEntityTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import static leaf.cosmere.api.Constants.Strings.CONTAINED_POWERS_FOUND;

public class HemalurgyEngLangGen extends LanguageProvider
{
	public HemalurgyEngLangGen(PackOutput output)
	{
		super(output, Hemalurgy.MODID, "en_us");
	}

	@Override
	protected void addTranslations()
	{
		addItemsAndBlocks();
		addEntities();
		addAdvancements();
		addManifestations();
		addAttributes();
		addPatchouli();
		addCreativeTabs();
		addTooltips();
		addDamageSources();
		addMobEffects();
		addCurioIdentifiers();
		addCurioModifiers();
		addConfigs();
		addCommands();
		addKeybindings();
		addStats();

		add("screen.hemalurgy.bind_points.title", "Bind Points");
		add("button.hemalurgy.bind_points", "Bind Points");
		add("group.hemalurgy.eyes", "Eyes");
		add("group.hemalurgy.linchpin", "Linchpin");
		add("group.hemalurgy.physical", "Physical");
		add("group.hemalurgy.mental", "Mental");
		add("group.hemalurgy.spiritual", "Spiritual");
		add("group.hemalurgy.temporal", "Temporal");
		add("message.hemalurgy.bind_point.occupied", "That bind point already has a spike");
		add("message.hemalurgy.bind_point.invalid", "That spike doesn't fit this bind point");
		add("message.hemalurgy.bind_point.no_valid_spikes", "No spikes in your inventory fit this bind point");
		add("message.hemalurgy.spike.placed", "Spiked %s %s");
		add("message.hemalurgy.spike.removed", "Removed %s %s");
		add("command.hemalurgy.vision.set", "Hemalurgy vision for %s set to %s");
		add("command.hemalurgy.vision.notify", "Your hemalurgy vision has been turned %s");
		add("state.hemalurgy.on", "on");
		add("state.hemalurgy.off", "off");

	}


	private void addItemsAndBlocks()
	{
		//Items and Blocks
		for (Item item : ForgeRegistries.ITEMS.getValues())
		{
			final ResourceLocation registryName = RegistryHelper.get(item);
			if (registryName.getNamespace().contentEquals(Hemalurgy.MODID))
			{
				String localisedString = StringHelper.fixCapitalisation(registryName.getPath());

				if (item instanceof HemalurgicSpikeItem)
				{
					String use = ((HemalurgicSpikeItem) item).getMetalType().getShortHemalurgicUseString();
					add("item.hemalurgy." + registryName.getPath() + ".tooltip", use);
				}

				add(item.getDescriptionId(), localisedString);
			}
		}
	}

	private void addEntities()
	{
		//Entities
		for (IEntityTypeProvider type : HemalurgyEntityTypes.ENTITY_TYPES.getAllEntityTypes())
		{
			final ResourceLocation id = type.getRegistryName();
			add(type.getEntityType().getDescriptionId(), StringHelper.fixCapitalisation(id.getPath()));
		}
	}

	private void addAdvancements()
	{

	}

	private void addManifestations()
	{
	}

	private void addAttributes()
	{
		final String descriptionId = HemalurgyAttributes.SPIRITWEB_INTEGRITY.getAttribute().getDescriptionId();
		String translation = descriptionId.split("\\.")[1];
		add(descriptionId, "Spiritweb Integrity");
	}

	private void addPatchouli()
	{
		//work through each metal and generate localisation for related things.
		for (Metals.MetalType metalType : EnumUtils.METAL_TYPES)
		{
			final String name = metalType.getName();
			String hKey = "hemalurgic_" + name;
			String hemalurgyGuide = "entry." + hKey;
			add(hemalurgyGuide, StringHelper.fixCapitalisation(name));
		}
	}

	private void addCreativeTabs()
	{
		//ItemGroups/Tabs
		add("tabs.hemalurgy.items", "Hemalurgy");

	}

	private void addTooltips()
	{
		add("tooltip.cosmere.attribute.iron", "%s%s Attack Damage");
		add("tooltip.cosmere.attribute.tin", "%s%s%% Better Night Vision");
		add("tooltip.cosmere.attribute.copper", "%s%s%% XP");
		add("tooltip.cosmere.attribute.zinc", "%s%s%% Emotional Fortitude");
		add("tooltip.cosmere.attribute.duralumin", "");//todo hemalurgic connection/identity
		add("tooltip.cosmere.attribute.chromium", "%s%s Luck");
		add("tooltip.cosmere.attribute.nicrosil", "");//todo hemalurgic investiture

		add("tooltip.hemalurgy.spike.pierce", "State: Pierce");
		add("tooltip.hemalurgy.spike.hold", "State: Hold");
		add("tooltip.hemalurgy.spike.toggle", "Shift + right-click to switch");

		add(CONTAINED_POWERS_FOUND, "Hemalurgic Charge:");
	}

	private void addDamageSources()
	{
		//Damage Sources
		add("death.attack.spiked", "%1$s was not careful with their hemalurgic spike");
		add("death.attack.spiked.player", "%1$s was not careful with their hemalurgic spike while fighting %2$s");
	}

	private void addMobEffects()
	{
	}

	private void addCurioIdentifiers()
	{
		add("curios.identifier.linchpin", "Linchpin Spike");
		add("curios.identifier.physical", "Physical Quadrant");
		add("curios.identifier.mental", "Mental Quadrant");
		add("curios.identifier.spiritual", "Spiritual Quadrant");
		add("curios.identifier.temporal", "Temporal Quadrant");
		add("curios.identifier.eyes", "Eye Spikes");
	}

	private void addCurioModifiers()
	{
		add("curios.modifiers.linchpin", "Linchpin Spike");
		add("curios.modifiers.physical", "Physical Quadrant");
		add("curios.modifiers.mental", "Mental Quadrant");
		add("curios.modifiers.spiritual", "Spiritual Quadrant");
		add("curios.modifiers.temporal", "Temporal Quadrant");
		add("curios.modifiers.eyes", "Eye Spikes");
	}

	private void addConfigs()
	{
		add("config.hemalurgy.integrityCheckTick", "Spiritweb Integrity Check Interval");
		add("config.hemalurgy.integrityCheckTick.desc", "How often, in ticks, the mod checks whether a player's spiritweb is still intact. Lower values react faster to missing spikes but cost slightly more performance. There are 20 ticks in one second.");
		add("config.hemalurgy.linchpinSpikeSpiritwebBonus", "Linchpin Spiritweb Bonus");
		add("config.hemalurgy.linchpinSpikeSpiritwebBonus.desc", "Spiritweb integrity bonus granted while a linchpin spike is equipped. Linchpin spikes occupy a special slot and grant this bonus on top of their metal's own spiritweb bonus.");
		add("config.hemalurgy.allomanticPewterSpiritwebBonus", "Allomantic Pewter Spiritweb Bonus");
		add("config.hemalurgy.allomanticPewterSpiritwebBonus.desc", "Extra spiritweb integrity granted when an Allomantic pewter spike is equipped in the linchpin slot. This stacks with the pewter spike's own spiritweb bonus.");
		add("config.hemalurgy.feruchemicalGoldSpiritwebBonus", "Feruchemical Gold Spiritweb Bonus");
		add("config.hemalurgy.feruchemicalGoldSpiritwebBonus.desc", "Extra spiritweb integrity granted when a Feruchemical gold spike is equipped in the linchpin slot. This stacks with the gold spike's own spiritweb bonus.");
		add("config.hemalurgy.spikePierceThreshold", "Spike Pierce Health %");
		add("config.hemalurgy.spikePierceThreshold.desc", "The percentage of maximum health a target must be below for you to pierce them with a spike. Targets below this percentage of their max health can be spiked. Set to 100 to allow spiking full-health targets.");
		add("config.hemalurgy.spikeCooldownTicks", "Spike Cooldown (Ticks)");
		add("config.hemalurgy.spikeCooldownTicks.desc", "How long, in ticks, a spike item must wait after piercing a target before it can be used again. There are 20 ticks in one second, so 1200 ticks equals one minute.");
		add("config.hemalurgy.spikeDamage", "Spike Placement Damage");
		add("config.hemalurgy.spikeDamage.desc", "Flat damage dealt to the target when a spike is placed on them. This is separate from the percent-based placement damage.");
		add("config.hemalurgy.maxSpikesPerEntity", "Max Spikes Per Target");
		add("config.hemalurgy.maxSpikesPerEntity.desc", "The maximum number of spikes that can be embedded in a single target at once. Once reached, no more spikes can be placed until some are removed.");
		add("config.hemalurgy.spikePlaceDamagePercent", "Spike Placement Damage %");
		add("config.hemalurgy.spikePlaceDamagePercent.desc", "Damage dealt as a percentage of the target's maximum health when a spike is placed. Higher values make spiking more lethal.");
		add("config.hemalurgy.spikeRemoveHealthThreshold", "Spike Removal Health %");
		add("config.hemalurgy.spikeRemoveHealthThreshold.desc", "A target must be at or below this percentage of their maximum health for you to remove a spike by right-clicking. This prevents removing spikes from healthy targets.");
		add("config.hemalurgy.spikeRemoveDamagePercent", "Spike Removal Damage %");
		add("config.hemalurgy.spikeRemoveDamagePercent.desc", "Damage dealt as a percentage of the target's maximum health when a spike is removed. Higher values make removal riskier.");
		add("config.hemalurgy.spikeAttackDamage", "Spike Attack Damage");
		add("config.hemalurgy.spikeAttackDamage.desc", "The bonus melee attack damage a spike item adds when held in the main or off hand. Defaults to 0 so spikes rely on placement and removal damage percentages. This does not affect the damage dealt when placing or removing spikes.");
		add("screen.hemalurgy.config.title", "Hemalurgy Config");
		add("config.hemalurgy.tab.general", "General");
		add("config.hemalurgy.spike.maxStrength", "Max Spike Strength");
		add("config.hemalurgy.spike.maxStrength.desc", "The maximum strength this spike can store from a killed target. Strength determines how much power or attribute the spike grants. Set to 0 for no cap.");
		add("config.hemalurgy.spike.spiritwebBonus", "Spiritweb Integrity Bonus");
		add("config.hemalurgy.spike.spiritwebBonus.desc", "Spiritweb integrity bonus granted while this spike is equipped. Positive values make your spiritweb harder to damage; negative values weaken it.");
		add("config.hemalurgy.spike.stealMultiplier", "Steal Strength Multiplier");
		add("config.hemalurgy.spike.stealMultiplier.desc", "Scales how much power or attribute this spike steals from a killed target. 1.0 steals the normal amount, below 1.0 steals less, and above 1.0 steals more.");
		add("message.hemalurgy.config.updated", "Hemalurgy config updated");
		add("message.hemalurgy.config.denied", "You don't have permission to change the Hemalurgy config");
	}

	private void addCommands()
	{
		add(Constants.Strings.SET_EYE_HEIGHT_SUCCESS, "Set eye height to %s");
	}

	private void addKeybindings()
	{
	}

	private void addStats()
	{
	}
}
