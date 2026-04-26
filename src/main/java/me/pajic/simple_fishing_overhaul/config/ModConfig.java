package me.pajic.simple_fishing_overhaul.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.pajic.simple_fishing_overhaul.SFO;

@Version(version = 1)
public class ModConfig extends Config {

    public ModConfig() {
        super(SFO.id("config"));
    }

	public ValidatedDouble catchRadius = new ValidatedDouble(2, 4, 1);
	public ValidatedFloat intrigueChance = new ValidatedFloat(0.33F, 1, 0);
	public ValidatedDouble intrigueRadius = new ValidatedDouble(3.5, 8, 2);
	@RequiresAction(action = Action.RESTART) public ValidatedBoolean netheriteFishingRod = new ValidatedBoolean();
	public ValidatedBoolean fishDropNoLoot = new ValidatedBoolean();
	public ValidatedBoolean allowFishingWithoutFish = new ValidatedBoolean(false);
}
