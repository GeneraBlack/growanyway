# Grow Anyway 0.2.0

Comprehensive bugfix and stability release for Minecraft 1.21.1 on NeoForge 21.1.218.

This release fixes critical duplication exploits, resolves client/server sync issues, guarantees safe tree growth around modded infrastructure without destruction, and delivers reliable drop multipliers for all trees and mature crops.

## Highlights & Bug Fixes

### 1. Robust Tree Detection & Drop Multipliers
- **BFS Tree Structure Traversal**: Implemented a breadth-first search (`isTreeLog`) that follows connected tree trunks, branches, and canopy foliage up to 32 blocks.
- **Order-Independent Felling**: Trees drop multiplied logs consistently whether chopped from bottom-to-top, top-to-bottom, or from the middle.
- **Exploit & Duplication Prevention**: Player-placed logs (building walls, pillars, bases) do not have natural leaf canopies (`!persistent`) and drop exactly 1 log. Placed saplings and persistent leaves also never receive bonus drops.
- **Clean ItemEntity Spawning**: Extra drops are spawned as clean, separate `ItemEntity` instances, eliminating `SynchedEntityData` client-desync issues where multiplied items previously failed to register on clients.

### 2. Mature Crop & Column Crop Validation
- **Crops**: Wheat, carrots, potatoes, beetroots, nether wart, cocoa, and sweet berries now only receive drop bonuses when fully mature (`isMaxAge()`).
- **Column Crops**: Sugar cane, cactus, and bamboo only multiply grown segments above the planted base block, preventing placement/harvest duplication loops.

### 3. Modded Infrastructure Protection During Tree Growth
- **Zero Destruction of Mod Blocks**: Modded cables, pipes, and machinery (`namespace != "minecraft"`) are dynamically masked via `FeatureBypassWorldGenLevel` so tree features can grow freely around them. Any `setBlock` or `destroyBlock` attempts by the tree generator onto masked mod blocks are safely intercepted, preventing block loss.
- **Extended Canopy Clearing Range**: Increased default `featureClearRadius` to 6 and `featureClearHeight` to 24 (configurable up to 16 and 64 respectively) to accommodate wide-canopy trees (e.g. Large Oak, Dark Oak, Jungle).

### 4. 2x2 Sapling Preservation & Non-Plant Safety
- **Mega Tree Verification**: When bonemealing a sapling within a 2x2 arrangement, 1x1 trees now only consume the clicked sapling, preserving the remaining 3 adjacent saplings.
- **Non-Plant Exclusion**: Filtered out `FireBlock`, `FrostedIceBlock`, and `ChorusFlowerBlock` from artificial stage increments.
- **Gradual Growth Passes**: Plants advance incrementally per configured pass rather than instantly maxing out in a single tick.

### 5. Dedicated-Server & Client Sync
- **Server-Only Deployment**: Fully compatible with vanilla clients; no client installation required on dedicated servers.
- **Client Prediction**: Added `GrowthLogic.canGrow` to allow proper hand-swing animation when using bonemeal.
- **Duplicate FX Cleanup**: Removed redundant manual `levelEvent(1505)` invocation to eliminate double particles and sounds.

## Build Target
- Minecraft: 1.21.1
- NeoForge: 21.1.218
- Java: 21