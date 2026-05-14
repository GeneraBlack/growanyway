Server-Only Deployment Notes
============================

Grow Anyway currently supports dedicated-server-only installation.

What This Means
===============

If you run a dedicated NeoForge server, you can place the Grow Anyway jar on the server and let players join without installing the mod on their clients.

Why This Works
==============

The current feature set changes only server-authoritative behavior:

- bonemeal results,
- random growth decisions,
- block drop amounts,
- feature placement checks for growth.

These are all resolved by the server. The client receives only ordinary Minecraft state changes such as:

- changed block states,
- spawned item entities,
- updated inventories,
- normal block update packets.

No custom client logic is required to understand those results.

Does Bonus Loot Break Server-Only Compatibility?
================================================

No.

Extra drops are applied by the server after the drop list is created. The client does not need to know why a block dropped more items than usual. It only sees the final spawned items or the final inventory contents.

Loader Compatibility
====================

The mod metadata sets:

- `displayTest="IGNORE_SERVER_VERSION"`

This tells the loader that a missing client-side copy should not be treated as a dedicated-server mismatch for this mod.

Limits
======

This does not mean the mod is optional everywhere.

- In singleplayer, the integrated server runs inside the client, so the local game still needs the mod.
- In LAN hosting, the host still needs the mod for the same reason.
- If future versions add client-required networking or custom synced content, the server-only deployment model may no longer be valid.

Recommended Dedicated Server Setup
==================================

1. Put the Grow Anyway jar into the server `mods` folder.
2. Start the server once to generate `world/serverconfig/growanyway-server.toml`.
3. Adjust config values to fit the modpack.
4. Let clients join without installing the mod, unless your modpack requires it for some other reason.

Verification Checklist
======================

After installation, verify these cases on a dedicated server:

1. A client without Grow Anyway can join successfully.
2. Bonemeal still accelerates crops and trees as expected.
3. Extra drops appear correctly.
4. Nearby non-vanilla infrastructure blocks no longer prevent tree growth in the targeted area.