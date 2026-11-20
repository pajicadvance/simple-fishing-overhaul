package me.pajic.simple_fishing_overhaul.fishing;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.mixin.FishingHookAccessor;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FishingSession {

	private final FishingHook hook;
	private final FishingHookAccessor timers;
	private final List<AbstractFish> lured = new ArrayList<>();
	private @Nullable AbstractFish targetFish = null;
	private int waterTicks = 0;
	private int approachTicks = 0;
	private int fishCheckCooldown = 0;
	private boolean fishNearby = false;

	public FishingSession(FishingHook hook) {
		this.hook = hook;
		this.timers = (FishingHookAccessor) hook;
	}

	public @Nullable AbstractFish getTargetFish() {
		return targetFish;
	}

	public boolean hasTarget() {
		return targetFish != null;
	}

	public void onSplash() {
		rollIntrigue(1);
	}

	public void tickCatching(Runnable vanillaCatching) {
		if (++waterTicks % SFO.CONFIG.intrigueRerollInterval.get() == 0) {
			expireInterest();
			rollIntrigue(SFO.CONFIG.intrigueRerollChanceMultiplier.get());
		}

		if (timers.sfo$getNibble() > 0) {
			tickBite(vanillaCatching);
			return;
		}

		if (timers.sfo$getTimeUntilHooked() > 0 && targetFish != null) {
			tickApproach(vanillaCatching);
			return;
		}

		boolean allowWithoutFish = SFO.CONFIG.allowFishingWithoutFish.get();
		boolean approaching = timers.sfo$getTimeUntilHooked() > 0;
		if (!approaching && !allowWithoutFish && !isFishNearby()) return;
		vanillaCatching.run();
		if (!approaching && timers.sfo$getTimeUntilHooked() > 0 && !selectTarget() && !allowWithoutFish) resetToLure();
	}

	public FishingCatch reelIn(ObjectArrayList<ItemStack> vanillaItems, LootParams params) {
		Player player = hook.getPlayerOwner();
		if (player == null) return new FishingCatch(vanillaItems, false);
		SFO.debugLog("Intercepting fishing loot");
		ServerLevel level = (ServerLevel) hook.level();
		ObjectArrayList<ItemStack> items = new ObjectArrayList<>();
		AbstractFish fish = targetFish;
		targetFish = null;
		boolean caughtFish = fish != null && isHooked(fish);
		if (caughtFish) {
			SFO.debugLog("Caught {} with id {}", fish.getDisplayName().getString(), fish.getUUID().toString());
			items.addAll(FishingLoot.rollFishLoot(level, fish, player));
			vanillaItems.stream().filter(itemStack -> !itemStack.is(ItemTags.FISHES)).forEach(items::add);
			fish.discard();
		} else {
			SFO.debugLog("No fish caught");
			if (SFO.CONFIG.allowFishingWithoutFish.get()) items.addAll(FishingLoot.rollJunkOrTreasure(level, hook, params));
		}
		SFO.debugLog("Substituting fishing loot");
		items.forEach(itemStack -> SFO.debugLog("{} {}",
                itemStack.getCount(), itemStack.getHoverName().getString()
        ));
		return new FishingCatch(items, caughtFish);
	}

	public void onRemove() {
		lured.forEach(this::cleanup);
		lured.clear();
		targetFish = null;
	}

	private void tickBite(Runnable vanillaCatching) {
		if (targetFish != null && !isValidTarget(targetFish)) timers.sfo$setNibble(1);
		vanillaCatching.run();
		if (timers.sfo$getNibble() <= 0) endBite();
	}

	private void tickApproach(Runnable vanillaCatching) {
		if (targetFish == null || !isValidTarget(targetFish)) {
			releaseTarget(false);
			if (!selectTarget()) {
				resetToLure();
				return;
			}
		} else if (++approachTicks > SFO.CONFIG.maxApproachTicks.get()) {
			SFO.debugLog("Approach timed out");
			releaseTarget(true);
			resetToLure();
			return;
		}
		AbstractFish fish = targetFish;
		timers.sfo$setTimeUntilHooked(hasArrived(fish) ? 1 : 3);
		FishingParticles.spawnApproachParticles((ServerLevel) hook.level(), hook, fish);
		vanillaCatching.run();
		if (timers.sfo$getNibble() > 0) startBite();
	}

	private float lureProgress() {
		return Mth.clamp((float) timers.sfo$getLureSpeed() / 300, 0, 1);
	}

	private List<AbstractFish> getFishInIntrigueArea() {
		float lureProgress = lureProgress();
		float radiusMult = Mth.lerp(lureProgress, 1, SFO.CONFIG.lureBonusMultiplier.get());
		float depthMult = Mth.lerp(lureProgress, 1, SFO.CONFIG.lureBonusMultiplier.get() * 2);
		return FishingUtil.getEligibleFish(
                hook, SFO.CONFIG.intrigueRadius.get() * radiusMult, SFO.CONFIG.intrigueDepth.get() * depthMult
        );
	}

	private List<AbstractFish> getFishInCatchArea() {
		double catchRadius = SFO.CONFIG.catchRadius.get();
		return FishingUtil.getEligibleFish(hook, catchRadius, catchRadius);
	}

	private void rollIntrigue(float chanceMultiplier) {
		long now = hook.level().getGameTime();
		RandomSource random = hook.getRandom();
		float chance = SFO.CONFIG.intrigueChance.get() * Mth.lerp(lureProgress(), 1, 2) * chanceMultiplier;
		List<AbstractFish> inRange = getFishInIntrigueArea();
		SFO.debugLog("{} fish in intrigue range", inRange.size());
		for (AbstractFish fish : inRange) {
			AbstractFishExtension extension = extension(fish);
			if (extension.sfo$getBoredUntil() > now || FishingUtil.isHookActive(extension.sfo$getHook())) continue;
			if (random.nextFloat() < chance) {
				extension.sfo$setHook(hook);
				extension.sfo$setInterestExpiry(now + randomBetween(
                        random, SFO.CONFIG.minInterestTicks.get(), SFO.CONFIG.maxInterestTicks.get()
                ));
				track(fish);
				SFO.debugLog("Intrigued {} with id {}",
                        fish.getDisplayName().getString(), fish.getUUID().toString()
                );
			}
		}
	}

	private void expireInterest() {
		long now = hook.level().getGameTime();
		lured.removeIf(fish -> {
			AbstractFishExtension extension = extension(fish);
			if (!fish.isAlive() || extension.sfo$getHook() != hook) return true;
			if (fish != targetFish && now > extension.sfo$getInterestExpiry()) {
				cleanup(fish);
				extension.sfo$setBoredUntil(now + SFO.CONFIG.boredTicks.get());
				SFO.debugLog("{} with id {} lost interest",
                        fish.getDisplayName().getString(), fish.getUUID().toString()
                );
				return true;
			}
			return false;
		});
	}

	private boolean isFishNearby() {
		if (--fishCheckCooldown <= 0) {
			fishCheckCooldown = 5;
			fishNearby = !getFishInCatchArea().isEmpty();
		}
		return fishNearby;
	}

	private boolean selectTarget() {
		Set<AbstractFish> candidates = new LinkedHashSet<>(getFishInCatchArea());
		for (AbstractFish fish : getFishInIntrigueArea()) {
			if (extension(fish).sfo$getHook() == hook) candidates.add(fish);
		}
		candidates.removeIf(fish -> {
			FishingHook fishHook = extension(fish).sfo$getHook();
			return fishHook != hook && FishingUtil.isTargetOf(fish, fishHook);
		});
		if (candidates.isEmpty()) {
			SFO.debugLog("No fish to approach");
			return false;
		}

		AbstractFish selected = pickWeighted(new ArrayList<>(candidates));
		AbstractFishExtension extension = extension(selected);
		extension.sfo$setHook(hook);
		extension.sfo$setInterestExpiry(hook.level().getGameTime() + Math.max(
                SFO.CONFIG.minInterestTicks.get(),
                SFO.CONFIG.maxInterestTicks.get()
        ));
		track(selected);
		targetFish = selected;
		approachTicks = 0;
		SFO.debugLog(
                "{} with id {} is approaching",
                selected.getDisplayName().getString(),
                selected.getUUID().toString())
        ;
		return true;
	}

	private AbstractFish pickWeighted(List<AbstractFish> fishes) {
		double[] weights = new double[fishes.size()];
		double total = 0;
		for (int i = 0; i < fishes.size(); i++) {
			AbstractFish fish = fishes.get(i);
			double weight = extension(fish).sfo$getHook() == hook ? SFO.CONFIG.intriguedFishWeight.get() : 1;
			weights[i] = weight / (1 + fish.distanceTo(hook));
			total += weights[i];
		}
		double roll = hook.getRandom().nextDouble() * total;
		for (int i = 0; i < fishes.size(); i++) {
			roll -= weights[i];
			if (roll < 0) return fishes.get(i);
		}
		return fishes.getLast();
	}

	private boolean isValidTarget(AbstractFish fish) {
		return fish.isAlive() && fish.level() == hook.level() && extension(fish).sfo$getHook() == hook;
	}

	private boolean hasArrived(AbstractFish fish) {
		double arrivalDistance = SFO.CONFIG.arrivalDistance.get();
		return fish.distanceToSqr(FishingUtil.approachTarget(hook)) <= arrivalDistance * arrivalDistance;
	}

	private boolean isHooked(AbstractFish fish) {
		double maxDistance = SFO.CONFIG.catchRadius.get() + 1;
		return timers.sfo$getNibble() > 0 &&
				isValidTarget(fish) &&
				extension(fish).sfo$isCaught() &&
				fish.distanceToSqr(hook) <= maxDistance * maxDistance;
	}

	private void startBite() {
		if (targetFish == null) return;
		extension(targetFish).sfo$setCaught(true);
		SFO.debugLog("Hooked {} with id {}",
                targetFish.getDisplayName().getString(), targetFish.getUUID().toString()
        );
	}

	private void endBite() {
		if (targetFish == null) return;
		SFO.debugLog("{} with id {} got away",
                targetFish.getDisplayName().getString(), targetFish.getUUID().toString()
        );
		releaseTarget(true);
	}

	private void resetToLure() {
		releaseTarget(false);
		timers.sfo$setTimeUntilHooked(0);
		timers.sfo$setTimeUntilLured(randomBetween(
                hook.getRandom(), SFO.CONFIG.minRelureTicks.get(), SFO.CONFIG.maxRelureTicks.get()
        ));
	}

	private void releaseTarget(boolean bored) {
		AbstractFish fish = targetFish;
		if (fish == null) return;
		targetFish = null;
		AbstractFishExtension extension = extension(fish);
		if (extension.sfo$getHook() != hook) return;
		extension.sfo$setCaught(false);
		if (bored) {
			extension.sfo$setHook(null);
			extension.sfo$setBoredUntil(hook.level().getGameTime() + SFO.CONFIG.boredTicks.get());
			lured.remove(fish);
		}
	}

	private void track(AbstractFish fish) {
		if (!lured.contains(fish)) lured.add(fish);
	}

	private void cleanup(AbstractFish fish) {
		AbstractFishExtension extension = extension(fish);
		if (extension.sfo$getHook() == hook) {
			extension.sfo$setCaught(false);
			extension.sfo$setHook(null);
		}
	}

	private static int randomBetween(RandomSource random, int first, int second) {
		return Mth.nextInt(random, Math.min(first, second), Math.max(first, second));
	}

	private static AbstractFishExtension extension(AbstractFish fish) {
		return (AbstractFishExtension) fish;
	}
}
