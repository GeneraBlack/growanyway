package de.growanyway.growanyway.growth;

import de.growanyway.growanyway.config.GrowAnywayConfig;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

public final class GrowAnywayEvents {
    private GrowAnywayEvents() {
    }

    public static void onBonemeal(BonemealEvent event) {
        if (!GrowAnywayConfig.SERVER.forceBonemealGrowth.get()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Player player = event.getPlayer();
        if (player != null && player.isSpectator()) {
            return;
        }

        if (!GrowthLogic.forceBonemeal(level, event.getPos(), event.getState())) {
            return;
        }

        if (player == null || !player.getAbilities().instabuild) {
            event.getStack().shrink(1);
        }

        level.levelEvent(1505, event.getPos(), 0);
        event.setSuccessful(true);
    }

    public static void onCropGrowPre(CropGrowEvent.Pre event) {
        if (!GrowAnywayConfig.SERVER.accelerateNaturalGrowth.get()) {
            return;
        }
        if (GrowthLogic.isPlantLikeBlock(event.getState())) {
            event.setResult(CropGrowEvent.Pre.Result.GROW);
        }
    }

    public static void onBlockDrops(BlockDropsEvent event) {
        if (!GrowAnywayConfig.SERVER.bonusPlantDrops.get()) {
            return;
        }

        int multiplier = GrowAnywayConfig.SERVER.bonusDropMultiplier.get();
        if (multiplier <= 1 || !GrowthLogic.shouldBoostDrops(event.getState())) {
            return;
        }

        event.getDrops().forEach(drop -> drop.getItem().grow(drop.getItem().getCount() * (multiplier - 1)));
    }

    public static void onBlockGrowFeature(BlockGrowFeatureEvent event) {
        if (!GrowAnywayConfig.SERVER.relaxFeatureSpaceChecks.get()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Holder<ConfiguredFeature<?, ?>> feature = event.getFeature();
        if (feature == null) {
            return;
        }

        if (GrowthLogic.tryPlaceFeatureIgnoringModBlocks(level, event.getPos(), feature, event.getRandom())) {
            event.setCanceled(true);
        }
    }
}