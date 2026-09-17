/*
 * File updated ~ 8 - 1 - 2025 ~ Leaf
 */

package leaf.cosmere.allomancy.common.effects;

import leaf.cosmere.allomancy.common.config.AllomancyConfigs;
import leaf.cosmere.api.cosmereEffect.AttributeModifierInfo;
import leaf.cosmere.api.cosmereEffect.CosmereEffect;
import leaf.cosmere.common.registry.AttributesRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;

public class PewterBurnEffect extends CosmereEffect
{
	private boolean checkedConfig = false;

	public PewterBurnEffect()
	{
		super();

		//equivalent to a speed potion for misting of 8 strength, double for flaring.
		addAttributeModifier(
				Attributes.MOVEMENT_SPEED,
				0.0125D,
				AttributeModifier.Operation.MULTIPLY_TOTAL);

		//equivalent to a haste potion for misting of 8 strength, double for flaring.
		addAttributeModifier(
				Attributes.ATTACK_SPEED,
				0.0125D,
				AttributeModifier.Operation.MULTIPLY_TOTAL);

		//3 damage at 8 strength, double for flare
		addAttributeModifier(
				Attributes.ATTACK_DAMAGE,
				0.375D,
				AttributeModifier.Operation.ADDITION);

		//0.25 knock back at 8 strength, double for flare
		addAttributeModifier(
				Attributes.ATTACK_KNOCKBACK,
				0.03125D,
				AttributeModifier.Operation.ADDITION);

	}

	//Configs aren't loaded yet when this effect is constructed during registration,
	//so defer the config check until the modifiers are first applied at runtime.
	@Override
	public Map<Attribute, AttributeModifierInfo> getAttributeModifiers()
	{
		if (!checkedConfig)
		{
			checkedConfig = true;
			if (AllomancyConfigs.SERVER.getConfigSpec().isLoaded()
					&& AllomancyConfigs.SERVER.PEWTER_EXTRA_DAMAGE_REDUCTION.get())
			{
				//damage resistance
				//0.125 * 9 = 1.125
				addAttributeModifier(
						AttributesRegistry.DETERMINATION.get(),//please forgive me for my sins anime god
						0.125D,
						AttributeModifier.Operation.ADDITION);
			}
		}
		return super.getAttributeModifiers();
	}

}
