package me.pajic.simple_fishing_overhaul.platform.fabric;

//? fabric {

import me.pajic.simple_fishing_overhaul.SFO;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		SFO.onInitialize();
	}
}
//?}
