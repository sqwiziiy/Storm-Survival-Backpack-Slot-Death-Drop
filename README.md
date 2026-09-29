# Storm Survival Backpack Slot Death Drop

Minecraft 1.20.1 Fabric compatibility mod that restores Traveler's Backpack's
native death placement for a backpack equipped in the Trinkets `chest/back`
slot when Graves is installed.

## Scope

- Minecraft 1.20.1 / Fabric
- Traveler's Backpack 9.1.55
- Trinkets 3.7.2
- Graves 1.0.0 interaction tested
- Hardcore Revival 20.1.19 interaction tested
- Only an equipped Traveler's Backpack in Trinkets `chest/back` is handled
- Normal PlayerInventory backpacks remain upstream/Graves-owned
- `keepInventory=true` is unchanged
- Downed/revivable Hardcore Revival state does not trigger the transfer

On terminal death with `keepInventory=false`, the mod calls Traveler's
Backpack's native placement path directly. It deliberately does not call
`BackpackDeathHelper.onPlayerDrops`, does not insert the backpack into vanilla
inventory, and does not create a loose ItemEntity on the successful path. The
Trinkets slot is cleared only after native placement succeeds, preserving the
backpack's contents, upgrades, fluids, custom name, tier, and other NBT.

## Dependencies

Required: Fabric API, Traveler's Backpack, and Trinkets.

Graves and Hardcore Revival are optional integrations; the terminal-death
behavior was validated with Graves 1.0.0 and Hardcore Revival 20.1.19.

## License and third-party mods

This compatibility source is MIT licensed. Traveler's Backpack, Trinkets,
Graves, Hardcore Revival, Fabric API, Minecraft, and their respective assets
are separate third-party software and are not bundled or redistributed here.
