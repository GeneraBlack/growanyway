package de.growanyway.growanyway;

import com.mojang.logging.LogUtils;
import de.growanyway.growanyway.config.GrowAnywayConfig;
import de.growanyway.growanyway.growth.GrowAnywayEvents;
import org.slf4j.Logger;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(GrowAnywayMod.MOD_ID)
public final class GrowAnywayMod {
    public static final String MOD_ID = "growanyway";
    public static final Logger LOGGER = LogUtils.getLogger();
    private final String version;

    public GrowAnywayMod(ModContainer modContainer) {
        version = modContainer.getModInfo().getVersion().toString();
        modContainer.registerConfig(ModConfig.Type.SERVER, GrowAnywayConfig.SERVER_SPEC);
        NeoForge.EVENT_BUS.addListener(GrowAnywayEvents::onBonemeal);
        NeoForge.EVENT_BUS.addListener(GrowAnywayEvents::onCropGrowPre);
        NeoForge.EVENT_BUS.addListener(GrowAnywayEvents::onBlockDrops);
        NeoForge.EVENT_BUS.addListener(GrowAnywayEvents::onBlockGrowFeature);
        LOGGER.info("Loading {} {}", MOD_ID, version);
    }
}
