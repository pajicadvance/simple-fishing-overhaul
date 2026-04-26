package me.pajic.simple_fishing_overhaul.util;

import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FishingUtil {

    @SuppressWarnings("resource")
	public static List<AbstractFish> getEligibleFish(FishingHook hook, double radius) {
        return hook.level()
                .getEntities(
						hook, new AABB(hook.blockPosition())
								.inflate(radius, 0, radius)
                                .expandTowards(0, -radius, 0),
                        e -> e instanceof AbstractFish)
				.stream()
                .filter(e -> e.getLootTable().isPresent())
                .map(e -> (AbstractFish) e).toList();
    }

	public static boolean shouldChargeCast(Player player) {
		return !((PlayerExtension) player).sfo$getShouldCastFishingRod() && (player.fishing == null || player.fishing.isRemoved());
	}
}
