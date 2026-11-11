/*
 * File updated ~ 11 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.gui;

import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.config.HemalurgyServerConfig;
import leaf.cosmere.hemalurgy.common.network.packets.UpdateHemalurgyConfigMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HemalurgyConfigScreen extends Screen
{
	private static final int COLUMNS = 2;
	private static final int ROWS = 6;
	private static final int MARGIN_X = 20;
	private static final int GAP_X = 16;
	private static final int TOP = 32;
	private static final int BOTTOM = 36;

	private final Screen parent;
	private final List<ConfigEntry> entries = createEntries();
	private final List<EditBox> editBoxes = new ArrayList<>();

	private int colWidth;
	private int rowHeight;
	private int gridTop;

	public HemalurgyConfigScreen(Screen parent)
	{
		super(Component.translatable("screen.hemalurgy.config.title"));
		this.parent = parent;
	}

	private static List<ConfigEntry> createEntries()
	{
		HemalurgyServerConfig config = HemalurgyConfigs.SERVER;
		List<ConfigEntry> list = new ArrayList<>();

		list.add(new ConfigEntry("integrityCheckTick", config.SPIRITWEB_INTEGRITY_TICK_CHECK));
		list.add(new ConfigEntry("lerasatiumMaxSpikeStrength", config.LERASATIUM_MAX_SPIKE_STRENGTH));
		list.add(new ConfigEntry("chromiumMaxSpikeStrength", config.CHROMIUM_MAX_SPIKE_STRENGTH));
		list.add(new ConfigEntry("powerMaxSpikeStrength", config.DEFAULT_POWER_MAX_SPIKE_STRENGTH));
		list.add(new ConfigEntry("linchpinSpikeSpiritwebBonus", config.LINCHPIN_SPIKE_SPIRITWEB_BONUS));
		list.add(new ConfigEntry("allomanticPewterSpiritwebBonus", config.ALLOMANTIC_PEWTER_SPIRITWEB_BONUS));
		list.add(new ConfigEntry("feruchemicalGoldSpiritwebBonus", config.FERUCHEMICAL_GOLD_SPIRITWEB_BONUS));
		list.add(new ConfigEntry("ironSpikeStrengthMultiplier", config.IRON_SPIKE_STRENGTH_MULTIPLIER));
		list.add(new ConfigEntry("spikePierceThreshold", config.SPIKE_PIERCE_THRESHOLD));
		list.add(new ConfigEntry("spikeCooldownTicks", config.SPIKE_COOLDOWN_TICKS));
		list.add(new ConfigEntry("spikeDamage", config.SPIKE_DAMAGE));
		list.add(new ConfigEntry("maxSpikesPerEntity", config.MAX_SPIKES_PER_ENTITY));

		return list;
	}

	@Override
	protected void init()
	{
		this.colWidth = (this.width - MARGIN_X * 2 - GAP_X) / COLUMNS;
		this.rowHeight = Math.max(26, Math.min(36, (this.height - TOP - BOTTOM) / ROWS));
		this.gridTop = TOP + Math.max(0, (this.height - TOP - BOTTOM - ROWS * this.rowHeight) / 2);

		for (int i = 0; i < this.entries.size(); i++)
		{
			ConfigEntry entry = this.entries.get(i);
			int column = i % COLUMNS;
			int row = i / COLUMNS;
			int x = MARGIN_X + column * (this.colWidth + GAP_X);
			int y = this.gridTop + row * this.rowHeight;

			EditBox editBox = new EditBox(this.font, x, y + 10, this.colWidth, this.rowHeight - 11, Component.translatable(entry.translationKey()));
			editBox.setMaxLength(16);
			editBox.setValue(entry.currentValue());
			this.addRenderableWidget(editBox);
			this.editBoxes.add(editBox);
		}

		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
				.bounds(this.width / 2 - 100, this.height - 28, 200, 20)
				.build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

		for (int i = 0; i < this.entries.size(); i++)
		{
			ConfigEntry entry = this.entries.get(i);
			int column = i % COLUMNS;
			int row = i / COLUMNS;
			int x = MARGIN_X + column * (this.colWidth + GAP_X);
			int y = this.gridTop + row * this.rowHeight;

			String label = this.font.plainSubstrByWidth(Component.translatable(entry.translationKey()).getString(), this.colWidth);
			guiGraphics.drawString(this.font, label, x, y, 0xFFFFFF);
		}
	}

	@Override
	public void onClose()
	{
		this.applyChanges();
		this.minecraft.setScreen(this.parent);
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	private void applyChanges()
	{
		Map<String, Double> values = new LinkedHashMap<>();
		for (int i = 0; i < this.entries.size(); i++)
		{
			String text = this.editBoxes.get(i).getValue().trim();
			try
			{
				values.put(this.entries.get(i).name(), Double.parseDouble(text));
			}
			catch (NumberFormatException ignored)
			{
			}
		}

		if (!values.isEmpty() && this.minecraft != null && this.minecraft.getConnection() != null)
		{
			Hemalurgy.packetHandler().sendToServer(new UpdateHemalurgyConfigMessage(values));
		}
	}

	private record ConfigEntry(String name, ForgeConfigSpec.ConfigValue<? extends Number> value)
	{
		String translationKey()
		{
			return "config.hemalurgy." + this.name;
		}

		String currentValue()
		{
			try
			{
				return this.value.get().toString();
			}
			catch (IllegalStateException e)
			{
				return this.value.getDefault().toString();
			}
		}
	}
}
