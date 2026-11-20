# Simple Fishing Overhaul

Ever thought that it's weird that when fishing, you aren't fishing the actual fish that are physically in the water? Even weirder, you can kill said fish to get them as an item, so what's the point of using the fishing rod and waiting when you can just go in the water and do that instead?

This mod makes it so that when you fish using a fishing rod, you are **fishing the actual fish that are in the water**, not some imaginary fish. It also overhauls the process of fishing and the way other loot such as junk or treasure is obtained to accommodate the change.

- In order to catch fish, they need to be in a certain range of the bobber.
- Hold right-click to charge the fishing rod - the longer you charge, the farther the hook gets thrown.
- When the bobber hits the water, fish have a small chance to become intrigued by it and move towards it. Fish that swim nearby later can also become intrigued, and intrigued fish eventually lose interest and swim away.
- The Lure enchantment increases the chance for fish to be intrigued by the bobber, as well as the area in which they can be intrigued.
- Instead of an imaginary fish, a real nearby fish swims up to the bobber before biting, leaving bubbles and a wake behind it. Intrigued and closer fish are more likely to be the ones that bite.
- While the bobber is splashing, the hooked fish is held under it. Reeling in gives you the items that you would get if you killed the fish. If you miss the bite, the fish gets away and ignores bobbers for a while.
- You cannot fish anything if there are no fish in the water.
- You can get junk or treasure alongside fish catches with vanilla drop rates.
- If you try killing the fish with a weapon, they drop nothing.
- Named fish and fish released from a bucket cannot be caught, so your aquarium is safe.

These changes do indirectly make treasure fishing less effective, as you can no longer fish in empty waters at all. However, a config option is provided to allow fishing in empty waters, where every catch gives junk or treasure using the vanilla weights between the two.

You can also upgrade the regular fishing rod to a Netherite Fishing Rod in the smithing table, which makes it fireproof and increases its durability.

**[Fish No Stuck](https://modrinth.com/mod/fish-no-stuck) is highly recommended** to fix several vanilla bugs causing fish to get stuck.

## Compatibility

- Compatible with any modded entity that is considered an `AbstractFish` with a non-empty loot table assigned to it.
- Modded fishing rods are compatible as long as they extend the vanilla `FishingRodItem`.
- Mods that change the behavior of the vanilla fishing rod are NOT compatible.
- If a mod adds fish items to the fishing loot table without adding the corresponding fish entities, those fish items will be unobtainable.

## Configuration

The following can be configured:
- Catch radius of the bobber
- Chance for fish to become intrigued by the bobber, and how often fish nearby get another chance
- Area and depth around the bobber where fish can become intrigued, and how much Lure increases them
- How long fish stay interested in the bobber, and how long they ignore bobbers afterward
- How much intrigued fish are preferred when choosing which fish bites
- How long a fish has to reach the bobber, how close it needs to get to bite, and how long to wait before another fish tries
- Cast charging and the minimum and maximum cast distance
- Toggle Netherite Fishing Rod on or off
- Ability to fish junk or treasure if there are no fish in the water (disabled by default)
- Ability to kill fish to get their drops (disabled by default)
- Protection for fish released from a bucket (enabled by default)
- Casting bar and custom first and third person casting animations (client)
