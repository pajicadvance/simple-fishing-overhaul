package me.pajic.simple_fishing_overhaul.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.hud.FishingRodCastBarRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		initHudLayers();
	}

	private static void initHudLayers() {
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.INFO_BAR, SFO.id("cast_bar"),
				(graphics, deltaTracker) ->
						FishingRodCastBarRenderer.getInstance().extractBackground(graphics, deltaTracker)
		);
	}
}
//?}
