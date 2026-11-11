/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ScreenEvent$Init$Post
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package leaf.cosmere.hemalurgy.client.events;

import leaf.cosmere.hemalurgy.client.gui.HemalurgyBindPointsButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="hemalurgy", bus=Mod.EventBusSubscriber.Bus.FORGE, value={Dist.CLIENT})
public class HemalurgyClientInventoryEvents {
    private static final int CREATIVE_X_OFFSET = 0;
    private static final int CREATIVE_Y_OFFSET = 0;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen) {
            InventoryScreen inventoryScreen = (InventoryScreen)screen;
            event.addListener((GuiEventListener)new HemalurgyBindPointsButton(inventoryScreen.getGuiLeft() + 75 - 2 - 13, inventoryScreen.getGuiTop() + 78 - 2 - 13));
        } else {
            screen = event.getScreen();
            if (screen instanceof CreativeModeInventoryScreen) {
                CreativeModeInventoryScreen creativeScreen = (CreativeModeInventoryScreen)screen;
                event.addListener((GuiEventListener)new HemalurgyBindPointsButton(creativeScreen.getGuiLeft() + 0, creativeScreen.getGuiTop() + 0));
            }
        }
    }
}
