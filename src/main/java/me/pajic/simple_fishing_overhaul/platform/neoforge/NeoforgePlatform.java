package me.pajic.simple_fishing_overhaul.platform.neoforge;

//? neoforge {

/*import me.pajic.simple_fishing_overhaul.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoforgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	@Override
	public String mcVersion() {
		return FMLLoader.getCurrent().getVersionInfo().mcVersion();
	}
}
*///?}
