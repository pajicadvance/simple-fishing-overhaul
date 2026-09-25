package me.pajic.simple_fishing_overhaul.goal;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;

public class GetLuredGoal<T extends AbstractFish> extends Goal {

    private final T fish;

    public GetLuredGoal(T fish) {
        this.fish = fish;
    }

	@Override
	public boolean canContinueToUse() {
		return !fish.getNavigation().isDone() || hookCheck();
	}

    @Override
    public boolean canUse() {
        return !((AbstractFishExtension) fish).sfo$isCaught() && hookCheck();
    }

    @Override
    public void start() {
		SFO.debugLog("Started GetLuredGoal for entity id {}", fish.getUUID().toString());
		move(1);
    }

    @Override
    public void tick() {
        if (hookCheck() && fish.distanceToSqr(getHook()) < SFO.CONFIG.catchRadius.get() * SFO.CONFIG.catchRadius.get()) move(0.5);
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

	private void move(double speed) {
		if (hookCheck()) {
			FishingHook h = getHook();
			fish.getNavigation().stop();
			fish.getNavigation().moveTo(h.getX(), h.getY() - SFO.CONFIG.catchRadius.get() + 1, h.getZ(), speed);
		}
	}

	private FishingHook getHook() {
		return ((AbstractFishExtension) fish).sfo$getHook();
	}

	private boolean hookCheck() {
		return getHook() != null && !getHook().isRemoved();
	}
}
