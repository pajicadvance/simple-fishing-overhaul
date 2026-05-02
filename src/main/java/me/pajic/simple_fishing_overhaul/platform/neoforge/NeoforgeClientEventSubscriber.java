package me.pajic.simple_fishing_overhaul.platform.neoforge;

//? neoforge {

/*import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.hud.FishingRodCastBarRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = SFO.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

	@SubscribeEvent
	public static void onClientSetup(final FMLCommonSetupEvent event) {
		SFO.onInitializeClient();
	}

	@SubscribeEvent
	private static void initHudLayers(RegisterGuiLayersEvent event) {
		event.registerAbove(
				VanillaGuiLayers.CONTEXTUAL_INFO_BAR, SFO.id("cast_bar"),
				(guiGraphics, deltaTracker) ->
						FishingRodCastBarRenderer.getInstance().extractBackground(guiGraphics, deltaTracker)
		);
	}
}
*///?}
