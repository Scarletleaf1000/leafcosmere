/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  leaf.cosmere.api.helpers.CuriosHelper
 *  leaf.cosmere.common.network.ICosmerePacket
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 *  top.theillusivec4.curios.api.CuriosApi
 *  top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
 */
package leaf.cosmere.hemalurgy.common.network.packets;

import java.util.Optional;
import leaf.cosmere.api.helpers.CuriosHelper;
import leaf.cosmere.common.network.ICosmerePacket;
import leaf.cosmere.hemalurgy.client.render.layer.BindPointTable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class UnequipSpikeMessage
implements ICosmerePacket {
    private final String key;

    public UnequipSpikeMessage(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.key);
    }

    public static UnequipSpikeMessage decode(FriendlyByteBuf buf) {
        return new UnequipSpikeMessage(buf.readUtf());
    }

    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            return;
        }
        String key = this.key;
        if (!BindPointTable.keys().contains(key)) {
            return;
        }
        int colon = key.indexOf(58);
        String group = key.substring(0, colon);
        int number = Integer.parseInt(key.substring(colon + 1));
        int curiosIndex = number - 1;
        Optional curiosInventory = CuriosApi.getCuriosInventory((LivingEntity)player).resolve();
        if (curiosInventory.isEmpty()) {
            return;
        }
        ItemStack stack = CuriosHelper.getStackInSlot((LivingEntity)player, (String)group, (int)curiosIndex);
        if (stack.isEmpty()) {
            return;
        }
        ((ICuriosItemHandler)curiosInventory.get()).setEquippedCurio(group, curiosIndex, ItemStack.EMPTY);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
