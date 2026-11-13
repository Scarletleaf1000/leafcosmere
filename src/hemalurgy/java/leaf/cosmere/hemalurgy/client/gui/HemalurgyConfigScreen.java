/*
 * File updated ~ 12 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.client.gui;

import leaf.cosmere.api.Metals;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.config.HemalurgyConfigs;
import leaf.cosmere.hemalurgy.common.config.HemalurgyServerConfig;
import leaf.cosmere.hemalurgy.common.network.packets.UpdateHemalurgyConfigMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HemalurgyConfigScreen extends Screen
{
	private static final int COLUMNS = 2;
	private static final int MARGIN_X = 20;
	private static final int GAP_X = 16;
	private static final int TOP = 30;
	private static final int TAB_HEIGHT = 18;
	private static final int TAB_GAP = 4;
	private static final int NAV_HEIGHT = 56;
	private static final int ROW_SPACING = 6;

	private final Screen parent;
	private final Map<String, String> values = new HashMap<>();
	private final List<ConfigEntry> generalEntries = generalEntries();

	private int currentTab = 0;
	private int currentPage = 0;
	private int colWidth;
	private int rowHeight;
	private int gridTop;
	private int entriesPerPage;
	private int totalPages;
	private int tabAreaBottom;

	public HemalurgyConfigScreen(Screen parent)
	{
		super(Component.translatable("screen.hemalurgy.config.title"));
		this.parent = parent;
	}

	private static List<ConfigEntry> generalEntries()
	{
		HemalurgyServerConfig c = HemalurgyConfigs.SERVER;
		List<ConfigEntry> list = new ArrayList<>();
		list.add(new ConfigEntry("integrityCheckTick", c.SPIRITWEB_INTEGRITY_TICK_CHECK, null));
		list.add(new ConfigEntry("spikePierceThreshold", c.SPIKE_PIERCE_THRESHOLD, null));
		list.add(new ConfigEntry("spikeCooldownTicks", c.SPIKE_COOLDOWN_TICKS, null));
		list.add(new ConfigEntry("spikeDamage", c.SPIKE_DAMAGE, null));
		list.add(new ConfigEntry("maxSpikesPerEntity", c.MAX_SPIKES_PER_ENTITY, null));
		list.add(new ConfigEntry("spikePlaceDamagePercent", c.SPIKE_PLACE_DAMAGE_PERCENT, null));
		list.add(new ConfigEntry("spikeRemoveHealthThreshold", c.SPIKE_REMOVE_HEALTH_THRESHOLD, null));
		list.add(new ConfigEntry("spikeRemoveDamagePercent", c.SPIKE_REMOVE_DAMAGE_PERCENT, null));
		list.add(new ConfigEntry("spikeAttackDamage", c.SPIKE_ATTACK_DAMAGE, null));
		list.add(new ConfigEntry("linchpinSpikeSpiritwebBonus", c.LINCHPIN_SPIKE_SPIRITWEB_BONUS, null));
		list.add(new ConfigEntry("allomanticPewterSpiritwebBonus", c.ALLOMANTIC_PEWTER_SPIRITWEB_BONUS, null));
		list.add(new ConfigEntry("feruchemicalGoldSpiritwebBonus", c.FERUCHEMICAL_GOLD_SPIRITWEB_BONUS, null));
		return list;
	}

	private static List<ConfigEntry> spikeEntries(Metals.MetalType metal)
	{
		HemalurgyServerConfig.SpikeTuning t = HemalurgyConfigs.SERVER.SPIKE_TUNING.get(metal);
		String p = "spike." + metal.getName() + ".";
		List<ConfigEntry> list = new ArrayList<>();
		list.add(new ConfigEntry(p + "maxStrength", t.maxStrength(), "config.hemalurgy.spike.maxStrength"));
		list.add(new ConfigEntry(p + "spiritwebBonus", t.spiritwebBonus(), "config.hemalurgy.spike.spiritwebBonus"));
		list.add(new ConfigEntry(p + "stealMultiplier", t.stealMultiplier(), "config.hemalurgy.spike.stealMultiplier"));
		return list;
	}

	private int tabCount()
	{
		return 1 + HemalurgyServerConfig.SPIKE_METALS.size();
	}

	private String tabLabel(int tab)
	{
		if (tab == 0)
		{
			return Component.translatable("config.hemalurgy.tab.general").getString();
		}
		String name = HemalurgyServerConfig.SPIKE_METALS.get(tab - 1).getName();
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private List<ConfigEntry> currentEntries()
	{
		if (this.currentTab == 0)
		{
			return this.generalEntries;
		}
		return spikeEntries(HemalurgyServerConfig.SPIKE_METALS.get(this.currentTab - 1));
	}

	@Override
	protected void init()
	{
		this.colWidth = (this.width - MARGIN_X * 2 - GAP_X) / COLUMNS;
		this.rowHeight = 18;
		this.rebuild();
	}

	private void rebuild()
	{
		this.clearWidgets();

		// tab buttons, flow-wrapped
		int tabY = TOP;
		int x = MARGIN_X;
		for (int t = 0; t < this.tabCount(); t++)
		{
			String label = this.tabLabel(t);
			int w = Math.max(46, this.font.width(label) + 8);
			if (x + w > this.width - MARGIN_X)
			{
				x = MARGIN_X;
				tabY += TAB_HEIGHT + 2;
			}
			final int tab = t;
			this.addRenderableWidget(Button.builder(Component.literal(label), b ->
			{
				this.currentTab = tab;
				this.currentPage = 0;
				this.rebuild();
			}).bounds(x, tabY, w, TAB_HEIGHT).build());
			x += w + TAB_GAP;
		}
		this.tabAreaBottom = tabY + TAB_HEIGHT;

		// entry grid pagination
		List<ConfigEntry> entries = this.currentEntries();
		int available = this.height - this.tabAreaBottom - 8 - NAV_HEIGHT;
		int rowsPerPage = Math.max(1, available / (this.rowHeight + ROW_SPACING + 12));
		this.entriesPerPage = COLUMNS * rowsPerPage;
		this.totalPages = Math.max(1, (entries.size() + this.entriesPerPage - 1) / this.entriesPerPage);
		this.currentPage = Mth.clamp(this.currentPage, 0, this.totalPages - 1);
		this.gridTop = this.tabAreaBottom + 8 + Math.max(0, (available - rowsPerPage * (this.rowHeight + ROW_SPACING + 12)) / 2);

		int start = this.currentPage * this.entriesPerPage;
		int end = Math.min(start + this.entriesPerPage, entries.size());
		for (int i = start; i < end; i++)
		{
			ConfigEntry entry = entries.get(i);
			int column = (i - start) % COLUMNS;
			int row = (i - start) / COLUMNS;
			int ex = MARGIN_X + column * (this.colWidth + GAP_X);
			int ey = this.gridTop + row * (this.rowHeight + ROW_SPACING + 12);

			EditBox editBox = new EditBox(this.font, ex, ey + 10, this.colWidth, this.rowHeight, Component.translatable(entry.translationKey()));
			editBox.setMaxLength(16);
			editBox.setValue(this.values.getOrDefault(entry.name(), entry.currentValue()));
			editBox.setResponder(text -> this.values.put(entry.name(), text));
			this.addRenderableWidget(editBox);
		}

		int navY = this.height - NAV_HEIGHT + 8;
		this.addRenderableWidget(Button.builder(Component.literal("<"), b -> this.changePage(-1))
				.bounds(this.width / 2 - 170, navY, 60, 20).build());
		this.addRenderableWidget(Button.builder(Component.literal(">"), b -> this.changePage(1))
				.bounds(this.width / 2 + 110, navY, 60, 20).build());
		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.onClose())
				.bounds(this.width / 2 - 50, this.height - 26, 100, 20).build());
	}

	private void changePage(int delta)
	{
		int newPage = Mth.clamp(this.currentPage + delta, 0, this.totalPages - 1);
		if (newPage != this.currentPage)
		{
			this.currentPage = newPage;
			this.rebuild();
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

		if (this.totalPages > 1)
		{
			String pageText = (this.currentPage + 1) + " / " + this.totalPages;
			guiGraphics.drawCenteredString(this.font, pageText, this.width / 2, this.height - NAV_HEIGHT + 2, 0xAAAAAA);
		}

		List<ConfigEntry> entries = this.currentEntries();
		int start = this.currentPage * this.entriesPerPage;
		int end = Math.min(start + this.entriesPerPage, entries.size());
		for (int i = start; i < end; i++)
		{
			ConfigEntry entry = entries.get(i);
			int column = (i - start) % COLUMNS;
			int row = (i - start) / COLUMNS;
			int ex = MARGIN_X + column * (this.colWidth + GAP_X);
			int ey = this.gridTop + row * (this.rowHeight + ROW_SPACING + 12);

			String label = this.font.plainSubstrByWidth(Component.translatable(entry.translationKey()).getString(), this.colWidth);
			guiGraphics.drawString(this.font, label, ex, ey, 0xFFFFFF);

			if (mouseX >= ex - 2 && mouseX <= ex + this.colWidth + 2 && mouseY >= ey - 2 && mouseY <= ey + 30)
			{
				guiGraphics.renderComponentTooltip(this.font, List.of(Component.translatable(entry.translationKey() + ".desc")), mouseX, mouseY);
			}
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
		Map<String, Double> parsed = new HashMap<>();
		for (Map.Entry<String, String> e : this.values.entrySet())
		{
			try
			{
				parsed.put(e.getKey(), Double.parseDouble(e.getValue().trim()));
			}
			catch (NumberFormatException ignored)
			{
			}
		}

		if (!parsed.isEmpty() && this.minecraft != null && this.minecraft.getConnection() != null)
		{
			Hemalurgy.packetHandler().sendToServer(new UpdateHemalurgyConfigMessage(parsed));
		}
	}

	private record ConfigEntry(String name, ForgeConfigSpec.ConfigValue<? extends Number> value, String langKey)
	{
		String translationKey()
		{
			return this.langKey != null ? this.langKey : "config.hemalurgy." + this.name;
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
