/*
 * File updated ~ 9 - 3 - 2025 ~ Leaf
 */

package leaf.cosmere.hemalurgy;

import leaf.cosmere.api.helpers.RegistryHelper;
import leaf.cosmere.api.providers.IItemProvider;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class HemalurgyItemModelsGen extends ItemModelProvider
{

	public HemalurgyItemModelsGen(PackOutput packOutput, ExistingFileHelper existingFileHelper)
	{
		super(packOutput, Hemalurgy.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		for (IItemProvider itemRegistryObject : HemalurgyItems.ITEMS.getAllItems())
		{
			String path = itemRegistryObject.getRegistryName().getPath();
			Item item = itemRegistryObject.asItem();

			if (item instanceof ForgeSpawnEggItem)
			{
				getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
				continue;
			}
			else if (item instanceof HemalurgicSpikeItem)
			{
				//pierced spikes use the vertically flipped model
				ModelFile pierceModel = this.getBuilder(path + "_pierce")
						.parent(new ModelFile.UncheckedModelFile(modLoc("item/spike")))
						.texture("layer0", modLoc("item/" + path));

				this.getBuilder(path)
						.parent(new ModelFile.UncheckedModelFile("item/generated"))
						.texture("layer0", modLoc("item/" + path))
						.override()
						.predicate(Hemalurgy.rl(HemalurgicSpikeItem.PIERCE_TAG), 1)
						.model(pierceModel)
						.end();
				continue;
			}
			else if (item instanceof SwordItem)
			{
				//koloss sword
				continue;
			}

			//else normal item texture rules apply
			simpleItem(path, path);
		}

	}

	public String getPath(Supplier<? extends Item> itemSupplier)
	{
		ResourceLocation location = RegistryHelper.get(itemSupplier.get());
		return location.getPath();
	}

	public ItemModelBuilder simpleItem(String path, String texturePath)
	{
		return this.getBuilder(path)
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", modLoc("item/" + texturePath));
	}
}