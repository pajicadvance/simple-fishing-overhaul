package me.pajic.simple_fishing_overhaul.gametest;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.gametest.mixin.FishingHookAccessor;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import me.pajic.simple_fishing_overhaul.util.FishingHookExtension;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FishingGameTests {

	private static final int POOL_DEPTH = 3;
	private static final BlockPos HOOK_POS = new BlockPos(3, POOL_DEPTH, 3);
	private static final Vec3 ANGLER_POS = new Vec3(3.5, 7.5, 3.5);
	private static final int MAX_TICKS = 1200;

	@GameTest
	public void eligibleFishExcludesProtectedAndInvalidFish(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		boolean protectReleased = SFO.CONFIG.protectReleasedFish.get();
		try {
			Cod unprotected = spawnCod(helper, 2.5, 1.5, 2.5);
			Cod named = spawnCod(helper, 4.5, 1.5, 2.5);
			named.setCustomName(Component.literal("Bob"));
			Cod released = spawnCod(helper, 2.5, 1.5, 4.5);
			released.setFromBucket(true);
			Cod dead = spawnCod(helper, 3.5, 1.5, 3.5);
			dead.kill(helper.getLevel());
			helper.spawn(EntityTypes.TADPOLE, new Vec3(4.5, 1.5, 4.5));
			FishingHook hook = castHook(helper, player, HOOK_POS);

			SFO.CONFIG.protectReleasedFish.accept(true);
			List<AbstractFish> eligible = FishingUtil.getEligibleFish(hook, 3, 3);
			helper.assertTrue(eligible.equals(List.of(unprotected)), "Expected only the unprotected cod, found " + eligible);

			SFO.CONFIG.protectReleasedFish.accept(false);
			eligible = FishingUtil.getEligibleFish(hook, 3, 3);
			helper.assertTrue(
					eligible.size() == 2 && eligible.contains(unprotected) && eligible.contains(released),
					"Expected the unprotected and released cod, found " + eligible
			);
			helper.succeed();
		} finally {
			SFO.CONFIG.protectReleasedFish.accept(protectReleased);
			cleanup(helper, player);
		}
	}

	@GameTest
	public void splashIntriguesOnlyAvailableFish(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		ServerPlayer otherPlayer = makeAngler(helper);
		float intrigueChance = SFO.CONFIG.intrigueChance.get();
		try {
			SFO.CONFIG.intrigueChance.accept(1F);
			Cod first = spawnCod(helper, 2.5, 1.5, 2.5);
			Cod second = spawnCod(helper, 4.5, 1.5, 4.5);
			Cod bored = spawnCod(helper, 2.5, 1.5, 4.5);
			extension(bored).sfo$setBoredUntil(helper.getLevel().getGameTime() + 1000);
			Cod claimed = spawnCod(helper, 4.5, 1.5, 2.5);
			FishingHook otherHook = castHook(helper, otherPlayer, new BlockPos(1, 6, 1));
			extension(claimed).sfo$setHook(otherHook);

			FishingHook hook = castHook(helper, player, HOOK_POS);
			hook.tick();

			helper.assertTrue(extension(first).sfo$getHook() == hook, "First cod was not intrigued");
			helper.assertTrue(extension(second).sfo$getHook() == hook, "Second cod was not intrigued");
			helper.assertTrue(extension(bored).sfo$getHook() == null, "Bored cod was intrigued");
			helper.assertTrue(extension(claimed).sfo$getHook() == otherHook, "Cod intrigued by another bobber was taken over");

			hook.discard();
			helper.assertTrue(extension(first).sfo$getHook() == null, "First cod kept the removed bobber");
			helper.assertTrue(extension(second).sfo$getHook() == null, "Second cod kept the removed bobber");
			helper.assertTrue(extension(claimed).sfo$getHook() == otherHook, "Removing a bobber cleared another bobber's cod");
			helper.succeed();
		} finally {
			SFO.CONFIG.intrigueChance.accept(intrigueChance);
			cleanup(helper, player);
			cleanup(helper, otherPlayer);
		}
	}

	@GameTest
	public void expiredInterestMakesFishBored(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		float intrigueChance = SFO.CONFIG.intrigueChance.get();
		try {
			SFO.CONFIG.intrigueChance.accept(1F);
			Cod cod = spawnCod(helper, 2.5, 1.5, 2.5);
			FishingHook hook = castHook(helper, player, HOOK_POS);
			hook.tick();
			helper.assertTrue(extension(cod).sfo$getHook() == hook, "Cod was not intrigued");

			extension(cod).sfo$setInterestExpiry(0);
			tickHook(hook, 45);

			long now = helper.getLevel().getGameTime();
			helper.assertTrue(extension(cod).sfo$getHook() == null, "Cod did not lose interest");
			helper.assertTrue(extension(cod).sfo$getBoredUntil() > now, "Cod did not become bored");
			helper.succeed();
		} finally {
			SFO.CONFIG.intrigueChance.accept(intrigueChance);
			cleanup(helper, player);
		}
	}

	@GameTest
	public void timersPauseWithoutFish(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		boolean allowWithoutFish = SFO.CONFIG.allowFishingWithoutFish.get();
		try {
			SFO.CONFIG.allowFishingWithoutFish.accept(false);
			FishingHook hook = castHook(helper, player, HOOK_POS);
			hook.tick();
			accessor(hook).sfotest$setTimeUntilLured(1);
			tickHook(hook, 10);

			helper.assertTrue(accessor(hook).sfotest$getTimeUntilLured() == 1, "Lure timer advanced without fish");
			helper.assertTrue(accessor(hook).sfotest$getTimeUntilHooked() == 0, "Approach started without fish");
			helper.assertTrue(accessor(hook).sfotest$getNibble() == 0, "Bite started without fish");
			helper.succeed();
		} finally {
			SFO.CONFIG.allowFishingWithoutFish.accept(allowWithoutFish);
			cleanup(helper, player);
		}
	}

	@GameTest
	public void emptyWaterCatchRollsJunkOrTreasure(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		boolean allowWithoutFish = SFO.CONFIG.allowFishingWithoutFish.get();
		try {
			SFO.CONFIG.allowFishingWithoutFish.accept(true);
			for (int i = 0; i < 20; i++) {
				FishingHook hook = castHook(helper, player, HOOK_POS);
				hook.tick();
				accessor(hook).sfotest$setNibble(20);
				hook.retrieve(player.getMainHandItem());

				List<ItemEntity> items = entitiesNear(helper, ItemEntity.class);
				helper.assertTrue(!items.isEmpty(), "Empty water catch gave nothing");
				helper.assertTrue(items.stream().noneMatch(item -> item.getItem().is(ItemTags.FISHES)), "Empty water catch gave a fish: " + items);
				helper.assertTrue(entitiesNear(helper, ExperienceOrb.class).isEmpty(), "Empty water catch gave experience");
				discardAll(helper, ItemEntity.class);
			}
			helper.succeed();
		} finally {
			SFO.CONFIG.allowFishingWithoutFish.accept(allowWithoutFish);
			cleanup(helper, player);
		}
	}

	@GameTest
	public void emptyWaterCatchGivesNothingWhenDisabled(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		boolean allowWithoutFish = SFO.CONFIG.allowFishingWithoutFish.get();
		try {
			SFO.CONFIG.allowFishingWithoutFish.accept(false);
			FishingHook hook = castHook(helper, player, HOOK_POS);
			hook.tick();
			accessor(hook).sfotest$setNibble(20);
			hook.retrieve(player.getMainHandItem());

			helper.assertTrue(entitiesNear(helper, ItemEntity.class).isEmpty(), "Empty water catch gave items while disabled");
			helper.assertTrue(entitiesNear(helper, ExperienceOrb.class).isEmpty(), "Empty water catch gave experience while disabled");
			helper.succeed();
		} finally {
			SFO.CONFIG.allowFishingWithoutFish.accept(allowWithoutFish);
			cleanup(helper, player);
		}
	}

	@GameTest
	public void pathTargetsStayInWater(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		helper.setBlock(5, 0, 5, Blocks.STONE);
		helper.setBlock(5, 1, 5, Blocks.STONE);
		helper.setBlock(5, 2, 5, Blocks.STONE);
		ServerPlayer player = makeAngler(helper);
		try {
			FishingHook deepHook = castHook(helper, player, HOOK_POS);
			double surfaceCenter = helper.absolutePos(HOOK_POS).getY() + 0.5;
			assertY(helper, FishingUtil.approachTarget(deepHook), surfaceCenter, "Approach target in deep water");
			assertY(helper, FishingUtil.waterTargetBelow(deepHook, 1), surfaceCenter - 1, "Hover target in deep water");

			FishingHook shallowHook = castHook(helper, player, new BlockPos(5, POOL_DEPTH, 5));
			assertY(helper, FishingUtil.waterTargetBelow(shallowHook, 3), surfaceCenter, "Hover target above a raised floor");
			helper.succeed();
		} finally {
			cleanup(helper, player);
		}
	}

	@GameTest(maxTicks = MAX_TICKS)
	public void fishApproachesAndIsHeldUnderBobber(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		Cod cod = spawnCod(helper, 5.5, 1.5, 5.5);
		FishingHook hook = castHook(helper, player, HOOK_POS);
		waitForBite(helper, hook, cod)
				.thenIdle(5)
				.thenExecuteFor(5, () -> helper.assertTrue(
						cod.distanceTo(hook) <= 1.0,
						"Hooked cod is " + cod.distanceTo(hook) + " blocks from the bobber"
				))
				.thenExecute(() -> cleanup(helper, player))
				.thenSucceed();
	}

	@GameTest(maxTicks = MAX_TICKS)
	public void reelingInHookedFishGivesFishLoot(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		Cod cod = spawnCod(helper, 5.5, 1.5, 5.5);
		FishingHook hook = castHook(helper, player, HOOK_POS);
		waitForBite(helper, hook, cod)
				.thenExecute(() -> {
					int damage = hook.retrieve(player.getMainHandItem());
					helper.assertTrue(cod.isRemoved(), "Caught cod was not removed");
					helper.assertTrue(damage == 1, "Expected 1 rod damage, got " + damage);
					List<ItemEntity> items = entitiesNear(helper, ItemEntity.class);
					helper.assertTrue(items.stream().anyMatch(item -> item.getItem().is(Items.COD)), "Catch did not contain a cod: " + items);
					int orbs = entitiesNear(helper, ExperienceOrb.class).size();
					helper.assertTrue(orbs == 1, "Expected 1 experience orb, found " + orbs);
				})
				.thenExecute(() -> cleanup(helper, player))
				.thenSucceed();
	}

	@GameTest(maxTicks = MAX_TICKS)
	public void reelingInRemovedFishGivesNothing(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		Cod cod = spawnCod(helper, 5.5, 1.5, 5.5);
		FishingHook hook = castHook(helper, player, HOOK_POS);
		waitForBite(helper, hook, cod)
				.thenExecute(() -> {
					cod.discard();
					hook.retrieve(player.getMainHandItem());
					helper.assertTrue(entitiesNear(helper, ItemEntity.class).isEmpty(), "Removed cod still gave loot");
					helper.assertTrue(entitiesNear(helper, ExperienceOrb.class).isEmpty(), "Removed cod still gave experience");
				})
				.thenExecute(() -> cleanup(helper, player))
				.thenSucceed();
	}

	@GameTest(maxTicks = MAX_TICKS)
	public void missedBiteReleasesFish(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		Cod cod = spawnCod(helper, 5.5, 1.5, 5.5);
		FishingHook hook = castHook(helper, player, HOOK_POS);
		waitForBite(helper, hook, cod)
				.thenExecute(() -> accessor(hook).sfotest$setNibble(1))
				.thenWaitUntil(() -> {
					helper.assertTrue(accessor(hook).sfotest$getNibble() == 0, "Bite did not end");
					helper.assertTrue(((FishingHookExtension) hook).sfo$getTargetFish() == null, "Bobber kept its target");
					helper.assertTrue(!extension(cod).sfo$isCaught(), "Cod is still marked as caught");
					helper.assertTrue(extension(cod).sfo$getHook() == null, "Cod is still attracted to the bobber");
					helper.assertTrue(extension(cod).sfo$getBoredUntil() > helper.getLevel().getGameTime(), "Cod did not become bored");
				})
				.thenExecute(() -> cleanup(helper, player))
				.thenSucceed();
	}

	@GameTest(maxTicks = MAX_TICKS)
	public void biteEndsWhenHookedFishDies(GameTestHelper helper) {
		buildPool(helper, POOL_DEPTH);
		ServerPlayer player = makeAngler(helper);
		Cod cod = spawnCod(helper, 5.5, 1.5, 5.5);
		FishingHook hook = castHook(helper, player, HOOK_POS);
		waitForBite(helper, hook, cod)
				.thenExecute(cod::discard)
				.thenWaitUntil(() -> {
					helper.assertTrue(accessor(hook).sfotest$getNibble() == 0, "Bite did not end after the cod was removed");
					helper.assertTrue(((FishingHookExtension) hook).sfo$getTargetFish() == null, "Bobber kept the removed cod as its target");
				})
				.thenExecute(() -> cleanup(helper, player))
				.thenSucceed();
	}

	private static GameTestSequence waitForBite(GameTestHelper helper, FishingHook hook, AbstractFish fish) {
		return helper.startSequence()
				.thenExecuteAfter(5, () -> accessor(hook).sfotest$setTimeUntilLured(1))
				.thenWaitUntil(() -> helper.assertTrue(
						((FishingHookExtension) hook).sfo$getTargetFish() == fish,
						"Cod was not chosen to approach the bobber"
				))
				.thenWaitUntil(() -> {
					helper.assertTrue(accessor(hook).sfotest$getNibble() > 0, "Bite has not started");
					helper.assertTrue(extension(fish).sfo$isCaught(), "Cod is not marked as caught");
				});
	}

	private static void buildPool(GameTestHelper helper, int depth) {
		for (int x = 0; x < 8; x++) {
			for (int z = 0; z < 8; z++) {
				boolean wall = x == 0 || z == 0 || x == 7 || z == 7;
				helper.setBlock(x, 0, z, Blocks.STONE);
				for (int y = 1; y <= depth; y++) {
					helper.setBlock(x, y, z, wall ? Blocks.STONE : Blocks.WATER);
				}
			}
		}
	}

	private static ServerPlayer makeAngler(GameTestHelper helper) {
		ServerPlayer player = helper.makeMockServerPlayerInLevel();
		player.setPos(helper.absoluteVec(ANGLER_POS));
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FISHING_ROD));
		return player;
	}

	private static Cod spawnCod(GameTestHelper helper, double x, double y, double z) {
		return helper.spawn(EntityTypes.COD, new Vec3(x, y, z));
	}

	private static FishingHook castHook(GameTestHelper helper, ServerPlayer player, BlockPos relative) {
		FishingHook hook = new FishingHook(player, helper.getLevel(), 0, 0);
		hook.setPos(helper.absoluteVec(Vec3.atCenterOf(relative)));
		hook.setDeltaMovement(Vec3.ZERO);
		helper.getLevel().addFreshEntity(hook);
		return hook;
	}

	private static void tickHook(FishingHook hook, int ticks) {
		for (int i = 0; i < ticks; i++) hook.tick();
	}

	private static FishingHookAccessor accessor(FishingHook hook) {
		return (FishingHookAccessor) hook;
	}

	private static AbstractFishExtension extension(AbstractFish fish) {
		return (AbstractFishExtension) fish;
	}

	private static void assertY(GameTestHelper helper, Vec3 target, double expectedY, String name) {
		helper.assertTrue(Math.abs(target.y - expectedY) < 1.0E-6, name + " expected y " + expectedY + ", got " + target.y);
	}

	private static AABB testArea(GameTestHelper helper) {
		return helper.getBounds().inflate(2);
	}

	private static <T extends Entity> List<T> entitiesNear(GameTestHelper helper, Class<T> type) {
		return helper.getLevel().getEntitiesOfClass(type, testArea(helper));
	}

	private static void discardAll(GameTestHelper helper, Class<? extends Entity> type) {
		entitiesNear(helper, type).forEach(Entity::discard);
	}

	private static void cleanup(GameTestHelper helper, ServerPlayer player) {
		if (player.fishing != null) player.fishing.discard();
		helper.getLevel().getServer().getPlayerList().remove(player);
		discardAll(helper, FishingHook.class);
		discardAll(helper, AbstractFish.class);
		discardAll(helper, ItemEntity.class);
		discardAll(helper, ExperienceOrb.class);
	}
}
