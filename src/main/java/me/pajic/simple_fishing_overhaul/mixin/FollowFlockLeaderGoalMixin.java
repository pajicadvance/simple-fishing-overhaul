package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FollowFlockLeaderGoal.class)
public class FollowFlockLeaderGoalMixin {

	@Shadow @Final private AbstractSchoolingFish mob;

	@WrapMethod(method = "canUse")
	private boolean checkLuredCanUse(Operation<Boolean> original) {
		return sfo$hookCheck(original);
	}

	@WrapMethod(method = "canContinueToUse")
	private boolean checkLuredCanContinue(Operation<Boolean> original) {
		return sfo$hookCheck(original);
	}

	@Unique boolean sfo$hookCheck(Operation<Boolean> original) {
		FishingHook hook = ((AbstractFishExtension) mob).sfo$getHook();
		return original.call() && (hook == null || hook.isRemoved());
	}
}
