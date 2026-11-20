package me.pajic.simple_fishing_overhaul.util;

import me.pajic.simple_fishing_overhaul.fishing.FishingSession;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import org.jetbrains.annotations.Nullable;

public interface FishingHookExtension {
    FishingSession sfo$getSession();

    default @Nullable AbstractFish sfo$getTargetFish() {
        return sfo$getSession().getTargetFish();
    }
}
