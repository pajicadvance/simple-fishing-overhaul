# Simple Fishing Overhaul

Ever thought that it's weird that when fishing, you aren't fishing the actual fish that are physically in the water? Even weirder, you can kill said fish to get them as an item, so what's the point of using the fishing rod and waiting when you can just go in the water and do that instead?

This mod makes it so that when you fish using a fishing rod, you are **fishing the actual fish that are in the water**, not some imaginary fish. It also overhauls the process of fishing and the way other loot such as junk or treasure is obtained to accommodate the change.

- In order to catch fish, they need to be in a certain range of the bobber.
- When the bobber starts splashing, a random nearby fish will get caught on the hook, and reeling in gives you the items that you would get if you killed the fish.
- When the bobber hits the water, fish have a small chance to become intrigued by it and move towards it, keeping them in range for the catch.
- The Lure enchantment increases the chance for fish to be intrigued by the bobber.
- You cannot fish anything if there are no fish in the water.
- You can get junk or treasure alongside fish catches with vanilla drop rates.
- If you try killing the fish with a weapon, they drop nothing.

These changes do indirectly make treasure fishing less effective, as you can no longer fish in empty waters at all. However, a config option is provided to allow fishing in empty waters, which allows fishing for junk and treasure without fish being present.

## Requirements

**Fabric**: [Fzzy Config](https://modrinth.com/mod/fzzy-config), [Fabric API](https://modrinth.com/mod/fabric-api), [Mod Menu](https://modrinth.com/mod/modmenu)

**NeoForge**: [Fzzy Config](https://modrinth.com/mod/fzzy-config)

**[Fish No Stuck](https://modrinth.com/mod/fish-no-stuck) is highly recommended** to fix several vanilla bugs causing fish to get stuck.

## Compatibility

- Compatible with any modded entity that is considered an `AbstractFish` with a loot table assigned to it.
- Modded fishing rods are compatible as long as they cast the vanilla fishing bobber.
- If a mod adds fish items to the fishing loot table without adding the corresponding fish entities, those fish items will be unobtainable.

## Configuration

The following can be configured:
- Catch radius of the bobber
- Chance for fish to become intrigued by the bobber
- Radius around the bobber where fish can become intrigued
- Ability to fish junk or treasure if there are no fish in the water (disabled by default)
- Ability to kill fish to get their drops (disabled by default)
