/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  leaf.cosmere.api.Metals$MetalType
 *  leaf.cosmere.common.registration.impl.ItemRegistryObject
 *  leaf.cosmere.hemalurgy.common.registries.HemalurgyItems
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.ImageButton
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package leaf.cosmere.hemalurgy.client.gui;

import leaf.cosmere.api.Metals;
import leaf.cosmere.common.registration.impl.ItemRegistryObject;
import leaf.cosmere.hemalurgy.client.gui.HemalurgyBindPointsScreen;
import leaf.cosmere.hemalurgy.common.registries.HemalurgyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class HemalurgyBindPointsButton
extends ImageButton {
    public static final int PREVIEW_RIGHT = 75;
    public static final int PREVIEW_BOTTOM = 78;
    public static final int BUTTON_WIDTH = 13;
    public static final int BUTTON_HEIGHT = 13;
    public static final int INSET = 2;
    public static final float ICON_SCALE = 0.35f;
    private static final ResourceLocation TEXTURE = new ResourceLocation("hemalurgy", "textures/gui/bind_points_button.png");
    private static final ItemStack IRON_SPIKE = new ItemStack((ItemLike)((ItemRegistryObject)HemalurgyItems.METAL_SPIKE.get(Metals.MetalType.IRON)).get());

    public HemalurgyBindPointsButton(int pX, int pY) {
        super(pX, pY, 13, 13, 0, 0, 0, TEXTURE, 13, 13, button -> Minecraft.getInstance().setScreen((Screen)new HemalurgyBindPointsScreen()));
        this.setTooltip(Tooltip.create((Component)Component.translatable((String)"button.hemalurgy.bind_points")));
    }

    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)this.getX() + 6.5f, (float)this.getY() + 6.5f, 0.0f);
        pGuiGraphics.pose().scale(0.35f, 0.35f, 1.0f);
        pGuiGraphics.renderItem(IRON_SPIKE, -8, -8);
        pGuiGraphics.pose().popPose();
    }
}
