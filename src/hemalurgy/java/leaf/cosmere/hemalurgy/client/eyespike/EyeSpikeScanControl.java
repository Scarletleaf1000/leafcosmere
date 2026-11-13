package leaf.cosmere.hemalurgy.client.eyespike;

import leaf.cosmere.allomancy.client.metalScanning.IronSteelLinesThread;
import leaf.cosmere.allomancy.common.capabilities.AllomancySpiritwebSubmodule;
import leaf.cosmere.allomancy.common.manifestation.AllomancyManifestation;
import leaf.cosmere.allomancy.common.registries.AllomancyManifestations;
import leaf.cosmere.api.Metals;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Hemalurgy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EyeSpikeScanControl
{
    private static boolean scanRunningForEyes = false;

    private EyeSpikeScanControl()
    {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
        {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
        {
            AllomancySpiritwebSubmodule.eyeSpikeVisionActive = false;
            stopIfOwned();
            return;
        }

        boolean eyeActive = EyeSpikeVision.isActive(player);
        AllomancySpiritwebSubmodule.eyeSpikeVisionActive = eyeActive;

        boolean wantScan = eyeActive && !isBurningIronOrSteel(player);

        if (wantScan && !scanRunningForEyes)
        {
            IronSteelLinesThread.startThread();
            scanRunningForEyes = true;
        }
        else if (!wantScan && scanRunningForEyes)
        {
            stopIfOwned();
        }
    }

    private static void stopIfOwned()
    {
        if (scanRunningForEyes)
        {
            IronSteelLinesThread.stopThread(false);
            scanRunningForEyes = false;
        }
    }

    /**
     * Real burn check, read only. True only when the player's iron or steel mode is non-zero.
     */
    public static boolean isBurningIronOrSteel(LocalPlayer player)
    {
        AllomancyManifestation iron = AllomancyManifestations.ALLOMANCY_POWERS.get(Metals.MetalType.IRON).get();
        AllomancyManifestation steel = AllomancyManifestations.ALLOMANCY_POWERS.get(Metals.MetalType.STEEL).get();
        return SpiritwebCapability.get(player).resolve()
                .map(spiritweb -> iron.getMode(spiritweb) != 0 || steel.getMode(spiritweb) != 0)
                .orElse(false);
    }
}
