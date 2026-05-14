Grow Anyway
===========

Grow Anyway is a NeoForge mod for Minecraft 1.21.1 that makes growth-focused gameplay much more forgiving and much more aggressive without breaking nearby modded infrastructure.

The current build targets NeoForge 21.1.218 and Java 21.

Features
========

- Forces bonemeal growth much more aggressively than vanilla.
- Pushes many modded plants with `age` or sapling `stage` properties forward even if they are not wired to vanilla bonemeal properly.
- Boosts drops from plants, leaves, and logs.
- Lets tree, mushroom, and azalea feature placement ignore nearby non-vanilla obstacle blocks during placement checks without deleting or replacing them.
- Supports dedicated-server-only installation. Clients can join without the mod installed.

Why This Mod Exists
===================

Vanilla growth logic is strict about space checks, random tick pacing, and which blocks count as valid bonemeal targets. That becomes a problem in modpacks where cables, pipes, machines, or utility blocks sit close to farms and saplings.

Grow Anyway focuses on three things:

1. Make bonemeal useful on far more plants.
2. Make passive growth faster.
3. Keep nearby modded blocks from blocking tree-like growth if those blocks are just infrastructure.

Server And Client Compatibility
===============================

Grow Anyway is designed to work as a dedicated-server-side mod.

- On a dedicated server, only the server needs Grow Anyway installed.
- Clients can connect without the mod because the mod currently does not add custom networking, custom synced registries, or client-required gameplay content.
- The mod metadata uses `displayTest="IGNORE_SERVER_VERSION"` so the loader will not treat the missing client-side copy as a mismatch.
- In singleplayer or LAN hosting, the host still needs the mod because the integrated server runs inside the game client.

More detail is available in [docs/server-only-deployment.md](docs/server-only-deployment.md).

Configuration
=============

After the first server start, the configuration file is created at `world/serverconfig/growanyway-server.toml`.

Key settings:

- `forceBonemealGrowth`: aggressively forces bonemeal-triggered growth.
- `bonemealPasses`: how many growth attempts are made for a single bonemeal use.
- `accelerateNaturalGrowth`: forces plant-like `CropGrowEvent` blocks to grow on random ticks.
- `bonusPlantDrops`: enables boosted drops for plant-like blocks and logs.
- `bonusDropMultiplier`: multiplier applied to matching drops.
- `relaxFeatureSpaceChecks`: enables non-destructive feature placement compatibility.
- `featureClearRadius`: horizontal scan radius for ignorable modded obstacles during feature placement.
- `featureClearHeight`: vertical scan height for ignorable modded obstacles during feature placement.

Technical Overview
==================

The mod is intentionally small and event-driven.

- Bonemeal forcing is implemented through `BonemealEvent`.
- Passive growth acceleration is implemented through `CropGrowEvent.Pre`.
- Bonus drops are implemented through `BlockDropsEvent`.
- Tree and feature compatibility is implemented through `BlockGrowFeatureEvent` and a masked `WorldGenLevel` view.

More detail is available in [docs/technical-overview.md](docs/technical-overview.md).

Development
===========

Requirements:

- JDK 21
- The Gradle wrapper included in this repository

Useful commands:

- `gradlew runClient` starts a development client.
- `gradlew runServer` starts a dedicated development server.
- `gradlew build` builds the mod jar into `build/libs`.

Repository Layout
=================

- `src/main/java/de/growanyway/growanyway` contains the mod entry points.
- `src/main/java/de/growanyway/growanyway/config` contains the server config.
- `src/main/java/de/growanyway/growanyway/growth` contains event handlers and growth logic.
- `src/main/resources/META-INF/neoforge.mods.toml` contains mod metadata.
- `.github/workflows/build.yml` contains the CI build workflow.

Build Status
============

The project includes a GitHub Actions workflow that runs `./gradlew build` on pushes and pull requests.

License
=======

This project is licensed under the MIT License. See [LICENSE](LICENSE).

References
==========

- NeoForge documentation: https://docs.neoforged.net/
- NeoForged Discord: https://discord.neoforged.net/

