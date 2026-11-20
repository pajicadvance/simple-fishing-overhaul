package me.pajic.simple_fishing_overhaul.goal;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public class GetLuredGoal<T extends AbstractFish> extends Goal {

    private final T fish;
	private int repathDelay;

    public GetLuredGoal(T fish) {
        this.fish = fish;
    }

    @Override
    public boolean canUse() {
        return !((AbstractFishExtension) fish).sfo$isCaught() && FishingUtil.isHookActive(getHook());
    }

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

    @Override
    public void start() {
		SFO.debugLog("Started GetLuredGoal for entity id {}", fish.getUUID().toString());
		repathDelay = 0;
    }

    @Override
    public void tick() {
		if (--repathDelay > 0) return;
		repathDelay = adjustedTickDelay(10);
		FishingHook hook = getHook();
		if (!FishingUtil.isHookActive(hook)) return;
		if (FishingUtil.isTargetOf(fish, hook)) {
			move(FishingUtil.approachTarget(hook), 1);
		} else if (fish.getNavigation().isDone()) {
			double catchRadius = SFO.CONFIG.catchRadius.get();
			move(FishingUtil.waterTargetBelow(hook, catchRadius - 1), fish.distanceToSqr(hook) < catchRadius * catchRadius ? 0.5 : 1);
		}
    }

	@Override
	public void stop() {
		SFO.debugLog("Stopped GetLuredGoal for entity id {}", fish.getUUID().toString());
	}

	@Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

	@Override
	public boolean isInterruptable() {
		return false;
	}

	private void move(Vec3 target, double speed) {
		fish.getNavigation().moveTo(target.x, target.y, target.z, speed);
	}

	private FishingHook getHook() {
		return ((AbstractFishExtension) fish).sfo$getHook();
	}
}
