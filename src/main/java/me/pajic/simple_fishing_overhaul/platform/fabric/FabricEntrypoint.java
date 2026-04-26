package me.pajic.simple_fishing_overhaul.platform.fabric;

//? fabric {

import me.pajic.simple_fishing_overhaul.SFO;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.simple_fishing_overhaul.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		SFO.onInitialize();
		initItems();
	}

	private static void initItems() {
		ModItems.init();
		Registry.register(
				BuiltInRegistries.ITEM,
				SFO.id("netherite_fishing_rod"),
				ModItems.NETHERITE_FISHING_ROD
		);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(contents ->
				contents.insertAfter(Items.FISHING_ROD, ModItems.NETHERITE_FISHING_ROD)
		);
	}
}
//?}
