package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.item.ModItems;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import me.pajic.simple_fishing_overhaul.util.PlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

//? neoforge
//import net.neoforged.neoforge.common.ItemAbility;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Unique private final FishingHook sfo$HOOK = (FishingHook) (Object) this;
    @Unique private AbstractFish sfo$hookedFish = null;
    @Unique private List<AbstractFish> sfo$lured = new ArrayList<>();

    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_BITING;
    @Shadow @Final private int lureSpeed;

	@ModifyArg(
			method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/FishingHook;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
			)
	)
	private Vec3 modifyCastDistance(Vec3 original, @Local(argsOnly = true, name = "player") final Player player) {
		double scale = Mth.clampedLerp(1 - (double) ((PlayerExtension) player).sfo$getRemainingCastTime() / 60, 0.25, 1.75);
		SFO.debugLog("Cast distance scale: {}", scale);
		return original.multiply(scale, scale, scale);
	}

    @SuppressWarnings("resource")
	@Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook$FishHookState;BOBBING:Lnet/minecraft/world/entity/projectile/FishingHook$FishHookState;",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 0
            )
    )
    private void onHookHitWater(CallbackInfo ci) {
		float lureMult = Mth.lerp(Mth.clamp((float) lureSpeed / 300, 0, 1), 1, 1.67F);
		sfo$lured = FishingUtil.getEligibleFish(sfo$HOOK, SFO.CONFIG.intrigueRadius.get() * lureMult);
		SFO.debugLog("{} fish in range", sfo$lured.size());
		sfo$lured.forEach(fish -> {
			if (sfo$HOOK.level().getRandom().nextFloat() < SFO.CONFIG.intrigueChance.get() * lureMult) {
				((AbstractFishExtension) fish).sfo$setHook(sfo$HOOK);
			}
		});
    }

	@WrapMethod(method = "catchingFish")
	private void canFishCheck(BlockPos blockPos, Operation<Void> original) {
		if (SFO.CONFIG.allowFishingWithoutFish.get()) original.call(blockPos);
		else if (!FishingUtil.getEligibleFish(sfo$HOOK, SFO.CONFIG.catchRadius.get()).isEmpty()) original.call(blockPos);
	}

	@SuppressWarnings("resource")
	@WrapMethod(method = "onSyncedDataUpdated")
	private void updateHookedFish(EntityDataAccessor<?> accessor, Operation<Void> original) {
        if (DATA_BITING.equals(accessor)) {
            boolean biting = sfo$HOOK.getEntityData().get(DATA_BITING);
            if (biting) {
                Level level = sfo$HOOK.level();
                List<AbstractFish> fishes = FishingUtil.getEligibleFish(sfo$HOOK, SFO.CONFIG.catchRadius.get());
                if (!fishes.isEmpty()) {
                    sfo$hookedFish = fishes.get(level.getRandom().nextInt(fishes.size()));
                    ((AbstractFishExtension) sfo$hookedFish).sfo$setHook(sfo$HOOK);
                    ((AbstractFishExtension) sfo$hookedFish).sfo$setCaught(true);
                    SFO.debugLog("Hooked {} with id {}", sfo$hookedFish.getDisplayName().getString(), sfo$hookedFish.getUUID().toString());
                }
				else SFO.debugLog("No fish in range");
            }
        }
        original.call(accessor);
    }

    @SuppressWarnings({"DataFlowIssue", "OptionalGetWithoutIsPresent"})
    @WrapOperation(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
                    ordinal = 0
            )
    )
    private boolean modifyFishingLoot(
            Level level,
            Entity entity,
            Operation<Boolean> original,
            @Share("caughtFish") LocalBooleanRef caughtFish,
			@Local(name = "items") List<ItemStack> items
    ) {
		caughtFish.set(false);
		Player player = sfo$HOOK.getPlayerOwner();
		if (player != null) {
			SFO.debugLog("Intercepting fishing loot");
			List<ItemStack> stacks = List.of();
			List<ItemStack> nonFishCatches = items.stream().filter(itemStack -> !itemStack.is(ItemTags.FISHES)).toList();
			if (sfo$hookedFish == null) {
				SFO.debugLog("No fish caught");
				if (SFO.CONFIG.allowFishingWithoutFish.get()) stacks = nonFishCatches;
			} else {
				SFO.debugLog("Caught {} with id {}", sfo$hookedFish.getDisplayName().getString(), sfo$hookedFish.getUUID().toString());
				stacks = new ArrayList<>(level.getServer().reloadableRegistries()
						.getLootTable(sfo$hookedFish.getLootTable().get())
						.getRandomItems(new LootParams.Builder((ServerLevel) level)
								.withParameter(LootContextParams.THIS_ENTITY, sfo$hookedFish)
								.withParameter(LootContextParams.ORIGIN, sfo$hookedFish.position())
								.withParameter(LootContextParams.DAMAGE_SOURCE, sfo$hookedFish.damageSources().playerAttack(player))
								.withParameter(LootContextParams.ATTACKING_ENTITY, player)
								.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
								.create(LootContextParamSets.ENTITY)
						));
				stacks.addAll(nonFishCatches);
				sfo$hookedFish.discard();
				sfo$hookedFish = null;
				caughtFish.set(true);
			}
			SFO.debugLog("Substituting fishing loot");
			stacks.forEach(itemStack -> {
				SFO.debugLog("{} {}", itemStack.getCount(), itemStack.getHoverName().getString());
				ItemEntity itemEntity = new ItemEntity(level, sfo$HOOK.getX(), sfo$HOOK.getY(), sfo$HOOK.getZ(), itemStack);
				double d = player.getX() - sfo$HOOK.getX();
				double e = player.getY() - sfo$HOOK.getY();
				double f = player.getZ() - sfo$HOOK.getZ();
				itemEntity.setDeltaMovement(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
				level.addFreshEntity(itemEntity);
			});
			entity.discard();
			return false;
		}
        return original.call(level, entity);
    }

    @WrapWithCondition(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/Identifier;I)V"
            )
    )
    private boolean statAwardCheck(Player instance, Identifier location, int count, @Share("caughtFish") LocalBooleanRef caughtFish) {
        return caughtFish.get();
    }

	@WrapOperation(
			method = "retrieve",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
					ordinal = 1
			)
	)
	private boolean xpAwardCheck(Level instance, Entity entity, Operation<Boolean> original, @Share("caughtFish") LocalBooleanRef caughtFish) {
		return caughtFish.get() ? original.call(instance, entity) : false;
	}

	@ModifyExpressionValue(
			method = "catchingFish",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
					ordinal = 0
			)
	)
	private boolean preventImaginaryFishParticles(boolean original) {
		return false;
	}

	@WrapOperation(
			method = "shouldStopFishing",
			at = @At(
					value = "INVOKE",
					//? fabric
					target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"
					//? neoforge
					//target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/neoforged/neoforge/common/ItemAbility;)Z"
			)
	)
	private boolean checkNetheriteRod(ItemStack instance, /*? fabric {*/Object/*?}*//*? neoforge {*//*ItemAbility*//*?}*/ item, Operation<Boolean> original) {
		return original.call(instance, item) || instance.is(ModItems.NETHERITE_FISHING_ROD);
	}

    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void onRemove(CallbackInfo ci) {
		sfo$lured.forEach(this::sfo$cleanup);
		if (sfo$hookedFish != null) {
			sfo$cleanup(sfo$hookedFish);
			sfo$hookedFish = null;
		}
    }

    @Unique private void sfo$cleanup(AbstractFish fish) {
        ((AbstractFishExtension) fish).sfo$setCaught(false);
		((AbstractFishExtension) fish).sfo$setHook(null);
    }
}
