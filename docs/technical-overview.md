Technical Overview
==================

This document explains how Grow Anyway changes gameplay behavior and why the implementation is compatible with dedicated-server-only installation.

Design Goals
============

Grow Anyway is built around four gameplay goals:

1. Make bonemeal reliably useful.
2. Speed up natural plant growth.
3. Increase harvest yields for growth-related blocks.
4. Stop nearby non-vanilla infrastructure blocks from blocking tree-like feature growth.

Implementation Model
====================

The mod is event-driven and server-authoritative.

- `BonemealEvent` handles forced bonemeal growth.
- `CropGrowEvent.Pre` handles accelerated random-tick growth.
- `BlockDropsEvent` handles extra drops.
- `BlockGrowFeatureEvent` handles feature placement compatibility for trees, huge mushrooms, azalea trees, and similar growth features.

No custom packets, synced custom registries, or client-only rendering hooks are required for the current feature set.

Forced Bonemeal Growth
======================

Grow Anyway performs several growth attempts per bonemeal use.

The logic tries the following, in order:

1. Special-case handling for blocks that vanilla does not bonemeal directly in the desired way, such as sugar cane and cactus.
2. Native `BonemealableBlock` behavior when the target supports it.
3. Direct promotion of compatible `age` or sapling `stage` properties.
4. A final fallback random tick for plant-like blocks that still rely on random ticking.

Special Cases
=============

- Sugar cane grows from the top of the current column, even if a lower segment is clicked.
- Cactus grows from the top of the current column, even if a lower segment is clicked.
- Bamboo is left on its native bonemeal path instead of being forced into an invalid fully-grown block state.

Accelerated Natural Growth
==========================

When enabled, Grow Anyway forces plant-like blocks that participate in `CropGrowEvent.Pre` to grow whenever that event is fired for them.

This is intentionally broad so that vanilla crops and many modded crops benefit without requiring per-mod integration.

Bonus Drops
===========

Bonus drops are applied in `BlockDropsEvent` after the server has already decided what will drop.

The multiplier is applied to:

- plant-like blocks,
- leaves,
- logs.

Because the change happens on the server-side drop list, clients only observe the final result as normal item entities or inventory changes.

Feature Placement Compatibility
===============================

The hardest compatibility problem in mixed modpacks is tree growth near pipes, cables, machine frames, and similar non-vanilla blocks.

Vanilla feature placement often assumes relatively clear 3x3 or 5x5 spaces. In modded bases that is unrealistic.

Grow Anyway solves this without deleting surrounding blocks:

1. It intercepts `BlockGrowFeatureEvent`.
2. It identifies nearby non-vanilla obstacle blocks inside a configurable radius and height.
3. It creates a delegating `WorldGenLevel` wrapper that makes those obstacles appear passable during placement checks.
4. It temporarily removes only the actual growth source block or sapling cluster needed for the feature call.
5. It places the feature through the wrapped world view.
6. It restores or synchronizes source blocks as needed.

This means nearby modded utility blocks can stop interfering with growth without being removed from the world.

Server-Only Behavior
====================

The mod is safe for dedicated-server-only installation because:

- all gameplay logic is executed on the server,
- the client only receives ordinary vanilla world updates,
- no custom networking is required,
- the mod metadata advertises client-optional compatibility through `displayTest="IGNORE_SERVER_VERSION"`.

Future Changes That Would Affect This
=====================================

The server-only deployment model would need to be revisited if the mod later adds any of the following:

- custom packets that the client must understand,
- custom synced registries,
- custom blocks, items, entities, menus, or screens that a vanilla client cannot render or decode,
- client-required HUD or rendering behavior tied to gameplay correctness.
