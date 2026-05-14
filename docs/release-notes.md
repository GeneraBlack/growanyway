Grow Anyway 0.1.0
==================

Initial public release for Minecraft 1.21.1 on NeoForge 21.1.218.

Highlights
==========

- Aggressive bonemeal support for many vanilla and modded plants.
- Faster passive growth for many plant blocks that participate in crop growth events.
- Increased drops from plants, leaves, and logs.
- Tree, mushroom, and azalea growth that can ignore nearby non-vanilla infrastructure blocks during feature placement checks.
- Dedicated-server-friendly deployment. Players can join without installing the mod on their clients.

Compatibility Notes
===================

- Sugar cane and cactus can be bonemealed through their full column logic.
- Bamboo stays on its native bonemeal path instead of being forced into an invalid growth state.
- Nearby modded pipes, cables, and machine blocks are not removed. They are only masked during feature placement checks.

Configuration
=============

The generated `growanyway-server.toml` file lets server owners tune:

- bonemeal forcing,
- bonemeal pass count,
- natural growth acceleration,
- bonus drop multiplier,
- feature-space compatibility radius and height.

Build Target
============

- Minecraft 1.21.1
- NeoForge 21.1.218
- Java 21