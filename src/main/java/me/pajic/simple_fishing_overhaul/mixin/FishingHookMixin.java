package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.pajic.simple_fishing_overhaul.fishing.FishingCatch;
import me.pajic.simple_fishing_overhaul.fishing.FishingSession;
import me.pajic.simple_fishing_overhaul.item.ModItems;
import me.pajic.simple_fishing_overhaul.util.FishingHookExtension;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? neoforge
//import net.neoforged.neoforge.common.ItemAbility;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin implements FishingHookExtension {

	@Unique private final FishingSession sfo$session = new FishingSession((FishingHook) (Object) this);

	@Override
	public FishingSession sfo$getSession() {
		return sfo$session;
	}

	@ModifyArg(
			method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/FishingHook;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
			)
	)
	private Vec3 modifyCastDistance(Vec3 original, @Local(argsOnly = true, name = "player") final Player player) {
		return FishingUtil.scaleCastVelocity(original, player);
	}

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
		if (!((FishingHook) (Object) this).level().isClientSide()) sfo$session.onSplash();
    }

	@WrapMethod(method = "catchingFish")
	private void wrapCatchingFish(BlockPos blockPos, Operation<Void> original) {
		sfo$session.tickCatching(() -> original.call(blockPos));
	}

    @WrapOperation(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
            )
    )
    private ObjectArrayList<ItemStack> substituteFishingLoot(
            LootTable lootTable,
            LootParams params,
            Operation<ObjectArrayList<ItemStack>> original,
            @Share("caughtFish") LocalBooleanRef caughtFish
    ) {
		FishingCatch fishingCatch = sfo$session.reelIn(original.call(lootTable, params), params);
		caughtFish.set(fishingCatch.caughtFish());
		return fishingCatch.items();
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
		if (!caughtFish.get()) return false;
		caughtFish.set(false);
		return original.call(instance, entity);
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
		return original && !sfo$session.hasTarget();
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
	private boolean checkNetheriteRod(
            ItemStack instance,
            /*? fabric {*/Object/*?}*//*? neoforge {*//*ItemAbility*//*?}*/ item,
            Operation<Boolean> original) {
		return original.call(instance, item) || instance.is(ModItems.NETHERITE_FISHING_ROD);
	}

    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void onRemove(CallbackInfo ci) {
		sfo$session.onRemove();
    }
}
