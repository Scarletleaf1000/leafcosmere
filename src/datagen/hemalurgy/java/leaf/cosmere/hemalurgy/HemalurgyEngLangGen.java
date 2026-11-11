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
		add("config.hemalurgy.integrityCheckTick", "Spiritweb Integrity Check Tick");
		add("config.hemalurgy.lerasatiumMaxSpikeStrength", "Lerasatium Max Spike Strength");
		add("config.hemalurgy.chromiumMaxSpikeStrength", "Chromium Max Spike Strength");
		add("config.hemalurgy.powerMaxSpikeStrength", "Power Max Spike Strength");
		add("config.hemalurgy.linchpinSpikeSpiritwebBonus", "Linchpin Spike Spiritweb Bonus");
		add("config.hemalurgy.allomanticPewterSpiritwebBonus", "Allomantic Pewter Spiritweb Bonus");
		add("config.hemalurgy.feruchemicalGoldSpiritwebBonus", "Feruchemical Gold Spiritweb Bonus");
		add("config.hemalurgy.ironSpikeStrengthMultiplier", "Iron Spike Strength Multiplier");
		add("config.hemalurgy.spikePierceThreshold", "Spike Pierce Threshold");
		add("config.hemalurgy.spikeCooldownTicks", "Spike Cooldown Ticks");
		add("config.hemalurgy.spikeDamage", "Spike Damage");
		add("config.hemalurgy.maxSpikesPerEntity", "Max Spikes Per Entity");
		add("config.hemalurgy.spikePlaceDamagePercent", "Spike Place Damage Percent");
		add("screen.hemalurgy.config.title", "Hemalurgy Config");
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
