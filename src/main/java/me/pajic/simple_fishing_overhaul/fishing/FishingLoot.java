package me.pajic.simple_fishing_overhaul.fishing;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class FishingLoot {

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static List<ItemStack> rollFishLoot(ServerLevel level, AbstractFish fish, Player player) {
		return level.getServer().reloadableRegistries()
				.getLootTable(fish.getLootTable().get())
				.getRandomItems(new LootParams.Builder(level)
						.withParameter(LootContextParams.THIS_ENTITY, fish)
						.withParameter(LootContextParams.ORIGIN, fish.position())
						.withParameter(LootContextParams.DAMAGE_SOURCE, fish.damageSources().playerAttack(player))
						.withParameter(LootContextParams.ATTACKING_ENTITY, player)
						.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
						.create(LootContextParamSets.ENTITY)
				);
	}

	public static List<ItemStack> rollJunkOrTreasure(ServerLevel level, FishingHook hook, LootParams params) {
		float luck = params.getLuck();
		int junkWeight = adjustedWeight(90, -2, luck);
		int treasureWeight = hook.isOpenWaterFishing() ? adjustedWeight(10, 2, luck) : 0;
		int total = junkWeight + treasureWeight;
		if (total <= 0) return List.of();
		ResourceKey<LootTable> table = hook.getRandom().nextInt(total) < junkWeight ?
                BuiltInLootTables.FISHING_JUNK : BuiltInLootTables.FISHING_TREASURE;
		return level.getServer().reloadableRegistries().getLootTable(table).getRandomItems(params);
	}

	private static int adjustedWeight(int weight, int quality, float luck) {
		return Math.max(Mth.floor(weight + quality * luck), 0);
	}
}
