/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.logging.LogUtils
 *  com.mojang.math.Axis
 *  leaf.cosmere.client.gui.CosmereScreen
 *  leaf.cosmere.hemalurgy.common.Hemalurgy
 *  leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.TooltipFlag$Default
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.slf4j.Logger
 *  top.theillusivec4.curios.api.CuriosApi
 *  top.theillusivec4.curios.api.SlotContext
 */
package leaf.cosmere.hemalurgy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import leaf.cosmere.client.gui.CosmereScreen;
import leaf.cosmere.hemalurgy.client.gui.BindPointReader;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointState;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointTable;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import leaf.cosmere.hemalurgy.common.network.packets.PlaceSpikeMessage;
import leaf.cosmere.hemalurgy.common.network.packets.UnequipSpikeMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class HemalurgyBindPointsScreen
extends CosmereScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation BACKGROUND = new ResourceLocation("hemalurgy", "textures/gui/hemalurgy_menu_background_dark.png");
    private static final int BG_WIDTH = 718;
    private static final int BG_HEIGHT = 720;
    private static final int MARGIN = 8;
    private static final float PANEL_ASPECT = 0.75f;
    private static final int PANEL_GAP = 4;
    private static final float MODEL_HEIGHT_UNITS = 32.0f;
    private static final int GRID_COLUMNS = 4;
    private static final boolean DEBUG_SHOW_ALL = false;
    private static final int COLOR_BORDER = -7368817;
    private static final int COLOR_SLOT_OUTLINE = -16777216;
    private static final int COLOR_SLOT_FILL = -11908534;
    private static final int COLOR_HIGHLIGHT = -1;
    private static final float HIT_UNITS = 2.5f;
    private static final boolean DEBUG_CUBE_POS = false;
    private static final int LIST_MAX_ROWS = 8;
    private static final int LIST_ROW_H = 18;
    private static final int LIST_PAD = 4;
    private static final List<SlotDef> SLOTS = HemalurgyBindPointsScreen.buildSlots();
    private static final Set<String> ALL_KEYS = HemalurgyBindPointsScreen.buildAllKeys();
    private int panelHeight;
    private int panelWidth;
    private float unit;
    private int modelScale;
    private int anchorY;
    private int cellPx;
    private int frontPanelX;
    private int backPanelX;
    private int panelTop;
    private boolean spikeListOpen;
    private String spikeListKey;
    private int spikeListX;
    private int spikeListY;
    private int spikeListW;
    private int spikeListH;
    private List<SpikeEntry> spikeEntries = List.of();
    private int spikeScroll;
    private final Map<String, float[]> cubePositions = new HashMap<String, float[]>();

    public HemalurgyBindPointsScreen() {
        super((Component)Component.translatable((String)"screen.hemalurgy.bind_points.title"), manifestation -> {});
    }

    private static List<SlotDef> buildSlots() {
        int i;
        ArrayList<SlotDef> slots = new ArrayList<SlotDef>();
        slots.add(new SlotDef("eyes", 1));
        slots.add(new SlotDef("eyes", 2));
        slots.add(new SlotDef("linchpin", 1));
        for (i = 1; i <= 10; ++i) {
            slots.add(new SlotDef("mental", i));
        }
        for (i = 1; i <= 8; ++i) {
            slots.add(new SlotDef("physical", i));
        }
        for (i = 1; i <= 4; ++i) {
            slots.add(new SlotDef("spiritual", i));
        }
        for (i = 1; i <= 16; ++i) {
            slots.add(new SlotDef("temporal", i));
        }
        Map<String, Integer> expected = Map.of("eyes", 2, "linchpin", 1, "physical", 8, "mental", 10, "spiritual", 4, "temporal", 16);
        HashMap<String, Integer> actual = new HashMap<String, Integer>();
        for (SlotDef slotDef : slots) {
            actual.merge(slotDef.group(), 1, Integer::sum);
        }
        for (Map.Entry entry : expected.entrySet()) {
            int count = actual.getOrDefault(entry.getKey(), 0);
            if (count == (Integer)entry.getValue()) continue;
            LOGGER.warn("Hemalurgy bind points screen: group {} has {} slots, expected {}", new Object[]{entry.getKey(), count, entry.getValue()});
        }
        return slots;
    }

    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        super.init();
        this.panelHeight = this.height - 16;
        this.panelWidth = (int)((float)this.panelHeight * 0.75f);
        this.unit = (float)this.panelHeight / 32.0f;
        this.modelScale = (int)(this.unit * 16.0f);
        this.cellPx = (int)(this.unit * 3.0f);
        int totalWidth = 2 * this.panelWidth + 4;
        this.frontPanelX = (this.width - totalWidth) / 2;
        this.backPanelX = this.frontPanelX + this.panelWidth + 4;
        this.panelTop = 8;
        this.anchorY = this.panelTop + this.panelHeight;
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        Map<String, ItemStack> stacks = BindPointReader.readStacks();
        Set<String> equipped = stacks.keySet();
        this.renderPanel(pGuiGraphics, this.backPanelX);
        BindPointState.setFor(ALL_KEYS, equipped, true);
        this.renderPlayerPanel(pGuiGraphics, this.backPanelX, true, equipped);
        BindPointState.clear();
        this.renderPanel(pGuiGraphics, this.frontPanelX);
        BindPointState.setFor(ALL_KEYS, equipped, false);
        this.renderPlayerPanel(pGuiGraphics, this.frontPanelX, false, equipped);
        BindPointState.clear();
        this.renderGrid(pGuiGraphics, equipped);
        String hoveredKey = this.renderCubeHover(pGuiGraphics, stacks, pMouseX, pMouseY);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        if (hoveredKey != null) {
            List<Component> tooltip = this.buildCubeTooltip(hoveredKey, stacks);
            pGuiGraphics.renderComponentTooltip(this.font, tooltip, pMouseX, pMouseY);
        }
        this.renderSpikeList(pGuiGraphics, pMouseX, pMouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int mx = (int)mouseX;
            int my = (int)mouseY;
            String key = this.cubeKeyAt(mx, my);
            if (key != null) {
                if (BindPointReader.readEquipped().contains(key)) {
                    this.closeSpikeList();
                    Hemalurgy.packetHandler().sendToServer((Object)new UnequipSpikeMessage(key));
                    return true;
                }
                this.openSpikeList(key, mx, my);
                return true;
            }
            if (this.spikeListOpen) {
                SpikeEntry entry = this.spikeEntryAt(mx, my);
                if (entry != null) {
                    Hemalurgy.packetHandler().sendToServer((Object)new PlaceSpikeMessage(this.spikeListKey, entry.inventoryIndex()));
                    this.closeSpikeList();
                    return true;
                }
                this.closeSpikeList();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.spikeListOpen && keyCode == 256) {
            this.closeSpikeList();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.spikeListOpen && this.spikeEntries.size() > 8) {
            this.spikeScroll = Mth.clamp((int)(this.spikeScroll - (int)Math.signum(delta)), (int)0, (int)(this.spikeEntries.size() - 8));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public void onClose() {
        BindPointState.clear();
        super.onClose();
    }

    public void removed() {
        BindPointState.clear();
        super.removed();
    }

    public void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
        this.renderPanelBackground(pGuiGraphics, this.backPanelX);
        this.renderPanelBackground(pGuiGraphics, this.frontPanelX);
    }

    private void renderPanel(GuiGraphics pGuiGraphics, int panelX) {
        pGuiGraphics.fill(panelX - 1, this.panelTop - 1, panelX + this.panelWidth + 1, this.panelTop + this.panelHeight + 1, -7368817);
    }

    private void renderPanelBackground(GuiGraphics pGuiGraphics, int panelX) {
        pGuiGraphics.enableScissor(panelX, this.panelTop, panelX + this.panelWidth, this.panelTop + this.panelHeight);
        pGuiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        pGuiGraphics.blit(BACKGROUND, panelX, this.panelTop, this.panelWidth, this.panelHeight, 0.0f, 0.0f, 718, 720, 718, 720);
        pGuiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        pGuiGraphics.disableScissor();
    }

    private void renderPlayerPanel(GuiGraphics pGuiGraphics, int panelX, boolean back, Set<String> equipped) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        int centreX = panelX + this.panelWidth / 2;
        if (back) {
            this.renderBackViewport(pGuiGraphics, centreX, (LivingEntity)player);
        } else {
            InventoryScreen.renderEntityInInventoryFollowsMouse((GuiGraphics)pGuiGraphics, (int)centreX, (int)this.anchorY, (int)this.modelScale, (float)0.0f, (float)0.0f, (LivingEntity)player);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderBackViewport(GuiGraphics pGuiGraphics, int centreX, LivingEntity player) {
        Quaternionf pose = new Quaternionf().rotateZ((float)Math.PI).mul((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        Quaternionf camera = new Quaternionf();
        float savedBodyRot = player.yBodyRot;
        float savedYRot = player.getYRot();
        float savedXRot = player.getXRot();
        float savedHeadRot = player.yHeadRot;
        float savedHeadRotO = player.yHeadRotO;
        player.yBodyRot = 180.0f;
        player.setYRot(180.0f);
        player.setXRot(0.0f);
        player.yHeadRot = 180.0f;
        player.yHeadRotO = 180.0f;
        try {
            InventoryScreen.renderEntityInInventory((GuiGraphics)pGuiGraphics, (int)centreX, (int)this.anchorY, (int)this.modelScale, (Quaternionf)pose, (Quaternionf)camera, (LivingEntity)player);
        }
        finally {
            player.yBodyRot = savedBodyRot;
            player.setYRot(savedYRot);
            player.setXRot(savedXRot);
            player.yHeadRot = savedHeadRot;
            player.yHeadRotO = savedHeadRotO;
        }
    }

    private Set<String> equippedSpikes() {
        return BindPointReader.readEquipped();
    }

    private static Set<String> buildAllKeys() {
        LinkedHashSet<String> keys = new LinkedHashSet<String>();
        for (SlotDef slot : SLOTS) {
            keys.add(HemalurgyBindPointsScreen.slotKey(slot));
        }
        return keys;
    }

    private static String slotKey(SlotDef slot) {
        return slot.group() + ":" + slot.number();
    }

    private String renderCubeHover(GuiGraphics pGuiGraphics, Map<String, ItemStack> stacks, int mouseX, int mouseY) {
        float k = this.unit / 4.0f;
        float hit = 2.5f * k;
        int frontCentreX = this.frontPanelX + this.panelWidth / 2;
        int backCentreX = this.backPanelX + this.panelWidth / 2;
        String hovered = null;
        float bestDist = Float.MAX_VALUE;
        float[] bestPos = null;
        this.cubePositions.clear();
        Iterator<String> iterator = BindPointTable.keys().iterator();
        while (iterator.hasNext()) {
            String key;
            boolean back;
            int centreX = (back = BindPointTable.isBackKey(key = iterator.next())) ? backCentreX : frontCentreX;
            float[] pos = BindPointTable.screenPosition(key, back, centreX, this.anchorY, k);
            if (pos == null) continue;
            this.cubePositions.put(key, pos);
            float dx = (float)mouseX - pos[0];
            float dy = (float)mouseY - pos[1];
            float dist = (float)Math.sqrt(dx * dx + dy * dy);
            if (!(dist <= hit) || !(dist < bestDist)) continue;
            bestDist = dist;
            hovered = key;
            bestPos = pos;
        }
        if (hovered == null) {
            return null;
        }
        float half = 2.0f * k;
        int x0 = Math.round((float)(bestPos[0] - half));
        int y0 = Math.round((float)(bestPos[1] - half));
        int x1 = Math.round((float)(bestPos[0] + half));
        int y1 = Math.round((float)(bestPos[1] + half));
        pGuiGraphics.fill(x0, y0, x1, y0 + 1, -1);
        pGuiGraphics.fill(x0, y1 - 1, x1, y1, -1);
        pGuiGraphics.fill(x0, y0, x0 + 1, y1, -1);
        pGuiGraphics.fill(x1 - 1, y0, x1, y1, -1);
        return hovered;
    }

    private List<Component> buildCubeTooltip(String key, Map<String, ItemStack> stacks) {
        ArrayList<Component> lines = new ArrayList<Component>();
        int colon = key.indexOf(58);
        String group = colon >= 0 ? key.substring(0, colon) : key;
        String number = colon >= 0 ? key.substring(colon + 1) : "";
        lines.add((Component)Component.translatable((String)("group.hemalurgy." + group)).append(" " + number));
        ItemStack stack = stacks.get(key);
        if (stack != null && !stack.isEmpty()) {
            lines.addAll(stack.getTooltipLines((Player)Minecraft.getInstance().player, (TooltipFlag)TooltipFlag.Default.NORMAL));
        }
        return lines;
    }

    private String cubeKeyAt(int mouseX, int mouseY) {
        float hit = 2.5f * (this.unit / 4.0f);
        String hovered = null;
        float bestDist = Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : this.cubePositions.entrySet()) {
            float dy;
            float[] pos = entry.getValue();
            float dx = (float)mouseX - pos[0];
            float dist = (float)Math.sqrt(dx * dx + (dy = (float)mouseY - pos[1]) * dy);
            if (!(dist <= hit) || !(dist < bestDist)) continue;
            bestDist = dist;
            hovered = entry.getKey();
        }
        return hovered;
    }

    private void openSpikeList(String key, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        ArrayList<SpikeEntry> entries = new ArrayList<SpikeEntry>();
        for (int i = 0; i <= 35; ++i) {
            this.addSpikeEntry(entries, mc.player.getInventory().getItem(i), i, key);
        }
        this.addSpikeEntry(entries, mc.player.getInventory().getItem(40), 40, key);
        this.spikeEntries = entries;
        this.spikeListKey = key;
        this.spikeScroll = 0;
        int rows = Math.max(1, Math.min(8, entries.size()));
        this.spikeListH = rows * 18 + 8;
        int maxNameWidth = 0;
        for (SpikeEntry entry : entries) {
            maxNameWidth = Math.max(maxNameWidth, this.font.width((FormattedText)entry.stack().getHoverName()));
        }
        if (entries.isEmpty()) {
            maxNameWidth = this.font.width((FormattedText)Component.translatable((String)"message.hemalurgy.bind_point.no_valid_spikes"));
        }
        this.spikeListW = Math.min(this.width - 16, maxNameWidth + 30);
        float[] pos = this.cubePositions.get(key);
        int cubeX = pos != null ? (int)pos[0] : mouseX;
        int cubeY = pos != null ? (int)pos[1] : mouseY;
        float cubeHalf = 2.0f * (this.unit / 4.0f);
        int x = (float)cubeX < (float)this.width / 2.0f ? (int)Math.ceil((float)cubeX + cubeHalf + 4.0f) : (int)Math.floor((float)cubeX - cubeHalf - 4.0f - (float)this.spikeListW);
        int y = Math.round((float)cubeY - (float)this.spikeListH / 2.0f);
        this.spikeListX = Mth.clamp((int)x, (int)8, (int)Math.max(8, this.width - this.spikeListW - 8));
        this.spikeListY = Mth.clamp((int)y, (int)8, (int)Math.max(8, this.height - this.spikeListH - 8));
        this.spikeListOpen = true;
    }

    private void addSpikeEntry(List<SpikeEntry> entries, ItemStack stack, int inventoryIndex, String key) {
        if (stack.isEmpty() || !(stack.getItem() instanceof HemalurgicSpikeItem)) {
            return;
        }
        if (!HemalurgyBindPointsScreen.isValidForKey(stack, key)) {
            return;
        }
        entries.add(new SpikeEntry(inventoryIndex, stack));
    }

    private static boolean isValidForKey(ItemStack stack, String key) {
        int number;
        int colon = key.indexOf(58);
        if (colon < 0) {
            return false;
        }
        String group = key.substring(0, colon);
        try {
            number = Integer.parseInt(key.substring(colon + 1));
        }
        catch (NumberFormatException e) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return false;
        }
        SlotContext slotContext = new SlotContext(group, (LivingEntity)mc.player, number - 1, false, true);
        return CuriosApi.isStackValid((SlotContext)slotContext, (ItemStack)stack);
    }

    private void closeSpikeList() {
        this.spikeListOpen = false;
        this.spikeListKey = null;
        this.spikeEntries = List.of();
        this.spikeScroll = 0;
    }

    private SpikeEntry spikeEntryAt(int mouseX, int mouseY) {
        if (!this.spikeListOpen || this.spikeEntries.isEmpty()) {
            return null;
        }
        if (mouseX < this.spikeListX || mouseX >= this.spikeListX + this.spikeListW || mouseY < this.spikeListY || mouseY >= this.spikeListY + this.spikeListH) {
            return null;
        }
        int row = (mouseY - this.spikeListY - 4) / 18;
        if (row < 0) {
            return null;
        }
        int index = this.spikeScroll + row;
        if (index >= this.spikeEntries.size() || row >= Math.min(8, this.spikeEntries.size() - this.spikeScroll)) {
            return null;
        }
        return this.spikeEntries.get(index);
    }

    private void renderSpikeList(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        if (!this.spikeListOpen) {
            return;
        }
        pGuiGraphics.flush();
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        RenderSystem.clear((int)256, (boolean)false);
        pGuiGraphics.fill(this.spikeListX - 1, this.spikeListY - 1, this.spikeListX + this.spikeListW + 1, this.spikeListY + this.spikeListH + 1, -16777216);
        pGuiGraphics.fill(this.spikeListX, this.spikeListY, this.spikeListX + this.spikeListW, this.spikeListY + this.spikeListH, -11908534);
        if (this.spikeEntries.isEmpty()) {
            pGuiGraphics.drawString(this.font, (Component)Component.translatable((String)"message.hemalurgy.bind_point.no_valid_spikes"), this.spikeListX + 4, this.spikeListY + 4, -1, false);
            return;
        }
        int visible = Math.min(8, this.spikeEntries.size() - this.spikeScroll);
        SpikeEntry hovered = null;
        for (int r = 0; r < visible; ++r) {
            SpikeEntry entry = this.spikeEntries.get(this.spikeScroll + r);
            int y = this.spikeListY + 4 + r * 18;
            pGuiGraphics.renderItem(entry.stack(), this.spikeListX + 4, y);
            pGuiGraphics.drawString(this.font, entry.stack().getHoverName(), this.spikeListX + 4 + 20, y + 4, -1, false);
            if (entry.stack().getCount() > 1) {
                String count = "x" + entry.stack().getCount();
                pGuiGraphics.drawString(this.font, count, this.spikeListX + this.spikeListW - 4 - this.font.width(count), y + 4, -5592406, false);
            }
            if (mouseX < this.spikeListX || mouseX >= this.spikeListX + this.spikeListW || mouseY < y || mouseY >= y + 18) continue;
            hovered = entry;
        }
        if (hovered != null) {
            List tooltip = hovered.stack().getTooltipLines((Player)Minecraft.getInstance().player, (TooltipFlag)TooltipFlag.Default.NORMAL);
            pGuiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
        pGuiGraphics.flush();
    }

    private void renderGrid(GuiGraphics pGuiGraphics, Set<String> equipped) {
        ArrayList<SlotDef> items = new ArrayList<SlotDef>();
        for (SlotDef slot : SLOTS) {
            if (!equipped.contains(HemalurgyBindPointsScreen.slotKey(slot))) continue;
            items.add(slot);
        }
        if (items.isEmpty()) {
            return;
        }
        int rows = (items.size() + 4 - 1) / 4;
        int gridWidth = 4 * this.cellPx;
        int gridHeight = rows * this.cellPx;
        int gridX = this.frontPanelX - gridWidth - 8;
        int gridY = (this.height - gridHeight) / 2;
        for (int i = 0; i < items.size(); ++i) {
            int x = gridX + i % 4 * this.cellPx;
            int y = gridY + i / 4 * this.cellPx;
            pGuiGraphics.fill(x - 1, y - 1, x + this.cellPx + 1, y + this.cellPx + 1, -16777216);
            pGuiGraphics.fill(x, y, x + this.cellPx, y + this.cellPx, -11908534);
        }
    }

    private record SlotDef(String group, int number) {
    }

    private record SpikeEntry(int inventoryIndex, ItemStack stack) {
    }
}
