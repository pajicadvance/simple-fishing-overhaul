package me.pajic.simple_fishing_overhaul.goal;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GetCaughtGoal<T extends AbstractFish> extends Goal {

    private final T fish;

    public GetCaughtGoal(T fish) {
        this.fish = fish;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return ((AbstractFishExtension) fish).sfo$isCaught() && FishingUtil.isHookActive(getHook());
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
		SFO.debugLog("Started GetCaughtGoal for entity id {}", fish.getUUID().toString());
        fish.getNavigation().stop();
    }

    @Override
    public void tick() {
		FishingHook hook = getHook();
		if (!FishingUtil.isHookActive(hook)) return;
		Vec3 pin = new Vec3(hook.getX(), hook.getY() - fish.getBbHeight() - 0.05, hook.getZ());
		fish.setDeltaMovement(pin.subtract(fish.position()).scale(0.3));
		float yRot = fish.getYRot() + (fish.getRandom().nextFloat() - 0.5F) * 60.0F;
		fish.setYRot(yRot);
		fish.yBodyRot = yRot;
    }

	@Override
	public void stop() {
		SFO.debugLog("Stopped GetCaughtGoal for entity id {}", fish.getUUID().toString());
	}

	@Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

	@Override
	public boolean isInterruptable() {
		return false;
	}

	private FishingHook getHook() {
		return ((AbstractFishExtension) fish).sfo$getHook();
	}
}
