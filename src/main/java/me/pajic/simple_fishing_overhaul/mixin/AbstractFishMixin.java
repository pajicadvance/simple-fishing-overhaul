package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.pajic.simple_fishing_overhaul.goal.GetCaughtGoal;
import me.pajic.simple_fishing_overhaul.goal.GetLuredGoal;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.class)
public abstract class AbstractFishMixin extends WaterAnimal implements AbstractFishExtension {

    protected AbstractFishMixin(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Unique @Nullable private FishingHook sfo$hook = null;
    @Unique private boolean sfo$caught = false;

    @Inject(
            method = "registerGoals",
            at = @At("TAIL")
    )
    private void addGetCaughtGoal(CallbackInfo ci) {
        AbstractFish fish = (AbstractFish) (Object) this;
        goalSelector.addGoal(0, new GetCaughtGoal<>(fish));
        goalSelector.addGoal(3, new GetLuredGoal<>(fish));
    }

	@Definition(id = "getTarget", method = "Lnet/minecraft/world/entity/animal/fish/AbstractFish;getTarget()Lnet/minecraft/world/entity/LivingEntity;")
	@Expression("this.getTarget() == null")
	@ModifyExpressionValue(
			//? if > 1.21.1
			method = "travelInWater",
			//? if 1.21.1
			//method = "travel",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private boolean extendTargetCheck(boolean original) {
		return sfo$hook == null ? original : original && sfo$hook.isRemoved();
	}

	@Override
	public void sfo$setHook(FishingHook hook) {
        this.sfo$hook = hook;
    }

    @Override @Nullable
    public FishingHook sfo$getHook() {
        return sfo$hook;
    }

    @Override
    public void sfo$setCaught(boolean caught) {
        this.sfo$caught = caught;
    }

    @Override
    public boolean sfo$isCaught() {
        return sfo$caught;
    }
}
