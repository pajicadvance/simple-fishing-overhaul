package me.pajic.simple_fishing_overhaul.util;

import net.minecraft.world.entity.projectile.FishingHook;

public interface AbstractFishExtension {
    void sfo$setHook(FishingHook hook);
    FishingHook sfo$getHook();
    void sfo$setCaught(boolean caught);
    boolean sfo$isCaught();
    void sfo$setInterestExpiry(long gameTime);
    long sfo$getInterestExpiry();
    void sfo$setBoredUntil(long gameTime);
    long sfo$getBoredUntil();
}
