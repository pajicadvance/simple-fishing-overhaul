package me.pajic.simple_fishing_overhaul.item;

import me.pajic.simple_fishing_overhaul.SFO;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class NetheriteFishingRodItem extends FishingRodItem {

	public NetheriteFishingRodItem() {
		super(new Properties()
				.fireResistant()
				.stacksTo(1)
				.durability(192)
				.repairable(Items.NETHERITE_INGOT)
				.enchantable(15)
				.setId(ResourceKey.create(Registries.ITEM, SFO.id("netherite_fishing_rod")))
		);
	}

	@Override
	public boolean isEnabled(@NotNull FeatureFlagSet enabledFeatures) {
		return SFO.CONFIG.netheriteFishingRod.get();
	}
}
