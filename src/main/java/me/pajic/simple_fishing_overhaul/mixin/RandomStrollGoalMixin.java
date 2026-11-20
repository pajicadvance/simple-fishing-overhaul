package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RandomStrollGoal.class)
public class RandomStrollGoalMixin {

	@Shadow @Final protected PathfinderMob mob;

	@WrapMethod(method = "canUse")
	private boolean checkLured(Operation<Boolean> original) {
		if (mob instanceof AbstractFish fish) {
			FishingHook hook = ((AbstractFishExtension) fish).sfo$getHook();
			if (hook != null && !hook.isRemoved()) return false;
		}
		return original.call();
	}
}
