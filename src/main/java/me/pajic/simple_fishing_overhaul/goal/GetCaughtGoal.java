package me.pajic.simple_fishing_overhaul.goal;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;

public class GetCaughtGoal<T extends AbstractFish> extends Goal {

    private final T fish;

    public GetCaughtGoal(T fish) {
        this.fish = fish;
    }

    @Override
    public boolean canUse() {
        return ((AbstractFishExtension) fish).sfo$isCaught();
    }

    @Override
    public void start() {
		SFO.debugLog("Started GetCaughtGoal for entity id {}", fish.getUUID().toString());
        FishingHook hook = ((AbstractFishExtension) fish).sfo$getHook();
        if (hook != null && !hook.isRemoved()) fish.setPos(hook.position());
    }

    @Override
    public boolean canContinueToUse() {
        return !canUse();
    }

    @Override
    public void tick() {
        start();
    }

	@Override
	public void stop() {
		((AbstractFishExtension) fish).sfo$setCaught(false);
		SFO.debugLog("Stopped GetCaughtGoal for entity id {}", fish.getUUID().toString());
	}

	@Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
