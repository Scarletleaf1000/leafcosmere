/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  leaf.cosmere.api.helpers.CuriosHelper
 *  leaf.cosmere.common.network.ICosmerePacket
 *  leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 *  top.theillusivec4.curios.api.CuriosApi
 *  top.theillusivec4.curios.api.SlotContext
 *  top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
 */
package leaf.cosmere.hemalurgy.common.network.packets;

import java.util.Optional;
import leaf.cosmere.api.helpers.CuriosHelper;
import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointTable;
import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class PlaceSpikeMessage
implements ICosmerePacket {
    private final String key;
    private final int inventoryIndex;

    public PlaceSpikeMessage(String key, int inventoryIndex) {
        this.key = key;
        this.inventoryIndex = inventoryIndex;
    }

    public String getKey() {
        return this.key;
    }

    public int getInventoryIndex() {
        return this.inventoryIndex;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.key);
        buf.writeVarInt(this.inventoryIndex);
    }

    public static PlaceSpikeMessage decode(FriendlyByteBuf buf) {
        return new PlaceSpikeMessage(buf.readUtf(), buf.readVarInt());
    }

    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            return;
        }
        String key = this.key;
        if (!BindPointTable.keys().contains(key)) {
            PlaceSpikeMessage.sendInvalid(player);
            return;
        }
        int colon = key.indexOf(58);
        String group = key.substring(0, colon);
        int number = Integer.parseInt(key.substring(colon + 1));
        int curiosIndex = number - 1;
        int invIndex = this.inventoryIndex;
        if ((invIndex < 0 || invIndex > 35) && invIndex != 40) {
            return;
        }
        ItemStack stack = player.getInventory().getItem(invIndex);
        if (!(stack.getItem() instanceof HemalurgicSpikeItem)) {
            return;
        }
        Optional curiosInventory = CuriosApi.getCuriosInventory((LivingEntity)player).resolve();
        if (curiosInventory.isEmpty()) {
            return;
        }
        SlotContext slotContext = new SlotContext(group, (LivingEntity)player, curiosIndex, false, true);
        if (!CuriosApi.isStackValid((SlotContext)slotContext, (ItemStack)stack)) {
            PlaceSpikeMessage.sendInvalid(player);
            return;
        }
        if (!CuriosHelper.getStackInSlot((LivingEntity)player, (String)group, (int)curiosIndex).isEmpty()) {
            player.sendSystemMessage((Component)Component.translatable((String)"message.hemalurgy.bind_point.occupied"));
            return;
        }
        ItemStack copy = stack.copy();
        copy.setCount(1);
        ((ICuriosItemHandler)curiosInventory.get()).setEquippedCurio(group, curiosIndex, copy);
        stack.shrink(1);
    }

    private static void sendInvalid(ServerPlayer player) {
        player.sendSystemMessage((Component)Component.translatable((String)"message.hemalurgy.bind_point.invalid"));
    }
}
