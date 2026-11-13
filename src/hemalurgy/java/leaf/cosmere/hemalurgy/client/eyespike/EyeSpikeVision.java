package leaf.cosmere.hemalurgy.client.eyespike;

import leaf.cosmere.hemalurgy.common.items.HemalurgicSpikeItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public final class EyeSpikeVision
{
    private static final String EYES_IDENTIFIER = "eyes";
    private static final int EYE_SLOT_COUNT = 2;

    private EyeSpikeVision()
    {
    }

    /**
     * True only when both eye slots of this player's Curios eyes group hold a Hemalurgic spike.
     * Read only. Never writes to Curios.
     */
    public static boolean isActive(Player player)
    {
        if (player == null)
        {
            return false;
        }
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.getStacksHandler(EYES_IDENTIFIER))
                .map(EyeSpikeVision::bothEyesFilled)
                .orElse(false);
    }

    private static boolean bothEyesFilled(ICurioStacksHandler handler)
    {
        var stacks = handler.getStacks();
        int filled = 0;
        for (int i = 0; i < EYE_SLOT_COUNT && i < stacks.getSlots(); i++)
        {
            ItemStack stack = stacks.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof HemalurgicSpikeItem)
            {
                filled++;
            }
        }
        return filled == EYE_SLOT_COUNT;
    }
}
