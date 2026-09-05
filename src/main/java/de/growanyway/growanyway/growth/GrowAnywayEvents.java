package de.growanyway.growanyway.growth;

import de.growanyway.growanyway.config.GrowAnywayConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

import java.util.ArrayList;
import java.util.List;

public final class GrowAnywayEvents {
    private GrowAnywayEvents() {
    }

    public static void onBonemeal(BonemealEvent event) {
        if (!GrowAnywayConfig.SERVER.forceBonemealGrowth.get()) {
            return;
        }

        Player player = event.getPlayer();
        if (player != null && player.isSpectator()) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (level.isClientSide()) {
            if (GrowthLogic.canGrow(level, pos, state)) {
                event.setSuccessful(true);
            }
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!GrowthLogic.forceBonemeal(serverLevel, pos, state)) {
            return;
        }

        if (player == null || !player.getAbilities().instabuild) {
            event.getStack().shrink(1);
        }

        // BoneMealItem.useOn plays levelEvent 1505 when event is successful,
        // so we avoid duplicate sound/particle playback here.
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
        if (multiplier <= 1 || !GrowthLogic.shouldBoostDrops(event.getLevel(), event.getPos(), event.getState())) {
            return;
        }

        List<ItemEntity> extraDrops = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            int originalCount = stack.getCount();
            long bonusTotal = (long) originalCount * (multiplier - 1);
            int maxStack = stack.getMaxStackSize();

            long remaining = bonusTotal;
            while (remaining > 0) {
                int toSpawn = (int) Math.min(remaining, maxStack);
                ItemStack extraStack = stack.copyWithCount(toSpawn);
                ItemEntity extraEntity = new ItemEntity(event.getLevel(), drop.getX(), drop.getY(), drop.getZ(), extraStack);
                extraEntity.setDefaultPickUpDelay();
                extraDrops.add(extraEntity);
                remaining -= toSpawn;
            }
        }

        event.getDrops().addAll(extraDrops);
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