/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  top.theillusivec4.curios.api.CuriosApi
 *  top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
 *  top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler
 */
package leaf.cosmere.hemalurgy.client.gui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class BindPointReader {
    private static final String[] GROUPS = new String[]{"eyes", "linchpin", "physical", "mental", "spiritual", "temporal"};

    private BindPointReader() {
    }

    public static Set<String> readEquipped() {
        return BindPointReader.readStacks().keySet();
    }

    public static Map<String, ItemStack> readStacks() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return Map.of();
        }
        return CuriosApi.getCuriosInventory((LivingEntity)player).resolve().map(BindPointReader::readStacksFromHandler).orElse(Map.of());
    }

    private static Map<String, ItemStack> readStacksFromHandler(ICuriosItemHandler handler) {
        LinkedHashMap<String, ItemStack> equipped = new LinkedHashMap<String, ItemStack>();
        for (String group : GROUPS) {
            handler.getStacksHandler(group).ifPresent(stacksHandler -> {
                IDynamicStackHandler stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacks.getSlots(); ++i) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!(stack.getItem() instanceof HemalurgicSpikeItem)) continue;
                    equipped.put(group + ":" + (i + 1), stack);
                }
            });
        }
        return equipped;
    }
}
