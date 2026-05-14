package de.growanyway.growanyway;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = GrowAnywayMod.MOD_ID, dist = Dist.CLIENT)
public final class GrowAnywayModClient {
    public GrowAnywayModClient() {
        GrowAnywayMod.LOGGER.debug("Client bootstrap initialized");
    }
}
