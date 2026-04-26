package me.pajic.simple_fishing_overhaul.platform.neoforge;

//? neoforge {

/*import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(SFO.MOD_ID)
@EventBusSubscriber(modid = SFO.MOD_ID)
public class NeoforgeEntrypoint {

	@SubscribeEvent
	private static void onCommonSetup(FMLCommonSetupEvent event) {
		SFO.onInitialize();
	}

	@SubscribeEvent
	private static void register(RegisterEvent event) {
		ModItems.init();
		event.register(
				Registries.ITEM,
				registry -> registry.register(SFO.id("netherite_fishing_rod"), ModItems.NETHERITE_FISHING_ROD)
		);
	}

	@SubscribeEvent
	private static void initItemGroups(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.insertAfter(
					Items.FISHING_ROD.getDefaultInstance(),
					ModItems.NETHERITE_FISHING_ROD.getDefaultInstance(),
					CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
			);
		}
	}
}
*///?}
