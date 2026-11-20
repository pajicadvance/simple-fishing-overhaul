package me.pajic.simple_fishing_overhaul.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.simple_fishing_overhaul.SFO;

@Version(version = 2)
public class ModConfig extends Config {

    public ModConfig() {
        super(SFO.id("config"));
    }

	public ValidatedDouble catchRadius = new ValidatedDouble(2, 4, 1);
	public ValidatedFloat intrigueChance = new ValidatedFloat(0.33F, 1, 0);
	public ValidatedDouble intrigueRadius = new ValidatedDouble(3, 8, 2);
    public ValidatedDouble intrigueDepth = new ValidatedDouble(9, 20, 4);
	public ValidatedFloat lureBonusMultiplier = new ValidatedFloat(1.67F, 2, 1);
	public ValidatedInt intrigueRerollInterval = new ValidatedInt(40, 400, 10);
	public ValidatedFloat intrigueRerollChanceMultiplier = new ValidatedFloat(0.25F, 1, 0);
	public ValidatedInt minInterestTicks = new ValidatedInt(400, 6000, 20);
	public ValidatedInt maxInterestTicks = new ValidatedInt(800, 6000, 20);
	public ValidatedInt boredTicks = new ValidatedInt(600, 6000, 0);
	public ValidatedFloat intriguedFishWeight = new ValidatedFloat(3, 10, 1);
	public ValidatedInt maxApproachTicks = new ValidatedInt(200, 1200, 20);
	public ValidatedDouble arrivalDistance = new ValidatedDouble(1.5, 4, 0.5);
	public ValidatedInt minRelureTicks = new ValidatedInt(20, 600, 1);
	public ValidatedInt maxRelureTicks = new ValidatedInt(60, 600, 1);
	public ValidatedBoolean castCharging = new ValidatedBoolean();
	public ValidatedDouble minCastDistanceMultiplier = new ValidatedDouble(0.25, 1, 0.1);
	public ValidatedDouble maxCastDistanceMultiplier = new ValidatedDouble(1.75, 2.5, 1);
	@RequiresAction(action = Action.RESTART) public ValidatedBoolean netheriteFishingRod = new ValidatedBoolean();
	public ValidatedBoolean fishDropNoLoot = new ValidatedBoolean();
	public ValidatedBoolean allowFishingWithoutFish = new ValidatedBoolean(false);
	public ValidatedBoolean protectReleasedFish = new ValidatedBoolean();

    @Override
    public void update(int deserializedVersion) {
        if (deserializedVersion == 1) {
            if (intrigueRadius.get() == 3.5) {
                intrigueRadius.validateAndSet(3.0);
            }
        }
    }
}
