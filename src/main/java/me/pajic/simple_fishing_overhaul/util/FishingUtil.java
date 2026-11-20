package me.pajic.simple_fishing_overhaul.util;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.mixin.LootTableAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishingUtil {

	private static final Map<ResourceKey<LootTable>, Boolean> LOOT_CACHE = new HashMap<>();
	private static @Nullable Object cachedRegistries = null;

	public static List<AbstractFish> getEligibleFish(FishingHook hook, double radius, double depth) {
		if (!(hook.level() instanceof ServerLevel level)) return List.of();
        return level
                .getEntities(
						hook, new AABB(hook.blockPosition())
								.inflate(radius, 0, radius)
                                .expandTowards(0, -depth, 0),
                        e -> e instanceof AbstractFish fish && fish.isAlive() && !isProtected(fish))
				.stream()
                .filter(e -> hasLoot(level, e))
                .map(e -> (AbstractFish) e).toList();
    }

	private static boolean isProtected(AbstractFish fish) {
		return fish.hasCustomName() || (SFO.CONFIG.protectReleasedFish.get() && fish.fromBucket());
	}

	private static boolean hasLoot(ServerLevel level, Entity entity) {
		Object registries = level.getServer().reloadableRegistries();
		if (registries != cachedRegistries) {
			LOOT_CACHE.clear();
			cachedRegistries = registries;
		}
		return entity.getLootTable()
				.map(key -> LOOT_CACHE.computeIfAbsent(key, k ->
						!((LootTableAccessor) level.getServer().reloadableRegistries().getLootTable(k))
								.sfo$getPools().isEmpty()))
				.orElse(false);
	}

	public static boolean isHookActive(@Nullable FishingHook hook) {
		return hook != null && !hook.isRemoved();
	}

	public static boolean isTargetOf(AbstractFish fish, @Nullable FishingHook hook) {
		return isHookActive(hook) && ((FishingHookExtension) hook).sfo$getTargetFish() == fish;
	}

	public static Vec3 approachTarget(FishingHook hook) {
		return waterTargetBelow(hook, 0.5);
	}

	public static Vec3 waterTargetBelow(FishingHook hook, double depth) {
		Level level = hook.level();
		int hookY = hook.blockPosition().getY();
		BlockPos.MutableBlockPos pos = BlockPos.containing(
                hook.getX(), hook.getY() - Math.max(depth, 0), hook.getZ()
        ).mutable();
		while (pos.getY() < hookY && !level.getFluidState(pos).is(FluidTags.WATER)) pos.move(Direction.UP);
		return new Vec3(hook.getX(), pos.getY() + 0.5, hook.getZ());
	}

	public static Vec3 scaleCastVelocity(Vec3 velocity, Player player) {
		if (!SFO.CONFIG.castCharging.get()) return velocity;
		double scale = Mth.clampedLerp(
				1 - (double) (player.getUseItemRemainingTicks() - 1) / 60,
				SFO.CONFIG.minCastDistanceMultiplier.get(),
				SFO.CONFIG.maxCastDistanceMultiplier.get()
		);
		SFO.debugLog("Cast distance scale: {}", scale);
		return velocity.scale(scale);
	}

	public static boolean shouldChargeCast(Player player) {
		return SFO.CONFIG.castCharging.get() &&
                !((PlayerExtension) player).sfo$getShouldCastFishingRod() &&
                (player.fishing == null || player.fishing.isRemoved());
	}
}
