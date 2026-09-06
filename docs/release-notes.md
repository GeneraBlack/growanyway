# Grow Anyway 0.3.0

Official port to **Minecraft 26.2** running on **NeoForge 26.2** with **Java 26**.

This release brings the full Grow Anyway feature set to the Minecraft 26.2 era, adapting to Mojang's new un-obfuscated codebase, modern NeoForge toolchain, and updated internal APIs while maintaining full server-only deployment capability and rock-solid gameplay stability.

## What's New in 0.3.0

### 1. Full Minecraft 26.2 & NeoForge 26.2 Compatibility
- **Modern Build System**: Migrated to `net.neoforged.moddev:2.0.146` with NeoForge `26.2.0.79` and Java 26 toolchain (`JavaLanguageVersion.of(26)`).
- **Identifier Migration**: Adapted codebase to Mojang's new `net.minecraft.resources.Identifier` and `ResourceKey.identifier()` APIs.
- **BlockItemTags Integration**: Updated plant and sapling tag references to the unified `BlockItemTags` registry.
- **Level & Redstone API Alignment**: Adapted `Level.neighborChanged` calls to the new `@Nullable Orientation` parameter and aligned `level.getRandom()` access.

### 2. Retained Core Features from v0.2.0
- **BFS Tree Detection & Drop Multipliers**: Connected canopy tree traversal guarantees 2x (configurable) log drops when felling trees from any direction, while player-placed logs without leaves drop normally.
- **Modded Infrastructure Protection**: `FeatureBypassWorldGenLevel` dynamic proxy protects modded machines, pipes, and cables during tree growth without obstructing natural canopies.
- **Mature Crop Safeguards**: Crops (wheat, carrots, potatoes, beetroots, nether wart, cocoa, sweet berries) and column crops (cane, cactus, bamboo) only multiply drops when fully grown.
- **Separate ItemEntity Spawning**: Drop bonuses are spawned as distinct item entities to prevent client-side synchronization issues.
- **Server-Only Deployment**: Fully server-side compatible; vanilla and modded clients can connect without needing the mod locally installed.

## Build & Platform Target
- **Minecraft**: 26.2
- **Mod Loader**: NeoForge 26.2.0.79+
- **Java**: 26 (supports Java 25+)