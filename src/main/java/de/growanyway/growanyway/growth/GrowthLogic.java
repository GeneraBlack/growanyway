package de.growanyway.growanyway.growth;

import de.growanyway.growanyway.config.GrowAnywayConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class GrowthLogic {
    private GrowthLogic() {
    }

    public static boolean canGrow(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof BonemealableBlock bonemealable) {
            return bonemealable.isValidBonemealTarget(level, pos, state);
        }
        if (state.getBlock() instanceof SugarCaneBlock || state.getBlock() instanceof CactusBlock) {
            BlockPos topPos = findColumnTop(level, pos, state.getBlock());
            return getColumnHeight(level, topPos, state.getBlock()) < 3 && level.isEmptyBlock(topPos.above());
        }
        if (isPlantLikeBlock(state)) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof IntegerProperty integerProperty && isSupportedGrowthProperty(state, integerProperty)) {
                    int currentValue = state.getValue(integerProperty);
                    int maxValue = integerProperty.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(currentValue);
                    if (currentValue < maxValue) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean forceBonemeal(ServerLevel level, BlockPos pos, BlockState initialState) {
        boolean changed = false;
        int passes = GrowAnywayConfig.SERVER.bonemealPasses.get();

        for (int attempt = 0; attempt < passes; attempt++) {
            BlockState currentState = level.getBlockState(pos);

            boolean attemptChanged = trySpecialBonemealGrowth(level, pos, currentState);
            if (currentState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
                attemptChanged = attemptChanged || applyBonemealable(level, pos, currentState, bonemealableBlock);
            }

            if (!attemptChanged) {
                attemptChanged = tryAdvanceGrowthState(level, pos, currentState);
            }

            if (!attemptChanged && currentState.isRandomlyTicking() && isPlantLikeBlock(currentState)) {
                currentState.randomTick(level, pos, level.random);
                attemptChanged = !level.getBlockState(pos).equals(currentState);
            }

            changed |= attemptChanged;
            if (!attemptChanged) {
                break;
            }
        }

        return changed || !level.getBlockState(pos).equals(initialState);
    }

    public static boolean tryPlaceFeatureIgnoringModBlocks(
            ServerLevel level,
            BlockPos eventPos,
            Holder<ConfiguredFeature<?, ?>> feature,
            RandomSource random
    ) {
        FeaturePlacementPlan plan = createFeaturePlacementPlan(level, eventPos, feature);
        Set<BlockPos> ignoredObstacles = collectIgnoredFeatureObstacles(level, plan.placementOrigin(), plan.sourceStates().keySet());
        if (ignoredObstacles.isEmpty()) {
            return false;
        }

        WorldGenLevel featureView = FeatureBypassWorldGenLevel.create(level, ignoredObstacles);
        Map<BlockPos, BlockState> replacementStates = removeFeatureSources(level, plan.sourceStates());
        boolean placed = feature.value().place(featureView, level.getChunkSource().getGenerator(), random, plan.placementOrigin());

        if (!placed) {
            restoreFeatureSources(level, plan.sourceStates());
            return false;
        }

        syncSourceRemovals(level, plan.sourceStates(), replacementStates);
        return true;
    }

    public static boolean isPlantLikeBlock(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof FireBlock || block instanceof FrostedIceBlock) {
            return false;
        }
        return block instanceof BonemealableBlock
                || block instanceof BushBlock
                || block instanceof GrowingPlantHeadBlock
                || block instanceof GrowingPlantBodyBlock
                || block instanceof SugarCaneBlock
                || block instanceof CactusBlock
                || block instanceof NetherWartBlock
                || block instanceof BambooStalkBlock
                || block instanceof BambooSaplingBlock
                || block instanceof VineBlock
                || state.is(BlockTags.SAPLINGS)
                || state.is(BlockTags.LEAVES);
    }

    public static boolean isNaturalLeaves(BlockState state) {
        if (state.is(BlockTags.LEAVES)) {
            return !state.hasProperty(LeavesBlock.PERSISTENT) || !state.getValue(LeavesBlock.PERSISTENT);
        }
        if (state.is(BlockTags.WART_BLOCKS) || state.is(Blocks.NETHER_WART_BLOCK) || state.is(Blocks.WARPED_WART_BLOCK) || state.is(Blocks.SHROOMLIGHT)) {
            return true;
        }
        return false;
    }

    public static boolean shouldBoostDrops(ServerLevel level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        // Placed saplings must never be duplicated
        if (state.is(BlockTags.SAPLINGS)) {
            return false;
        }

        // Natural leaves from trees (player-placed leaves have PERSISTENT = true)
        if (state.is(BlockTags.LEAVES)) {
            return isNaturalLeaves(state);
        }

        // Logs: only boost if part of a real tree connected to natural leaves
        if (state.is(BlockTags.LOGS)) {
            return isTreeLog(level, pos, state);
        }

        // Fully grown crops
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }
        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }
        if (block instanceof SweetBerryBushBlock) {
            return state.getValue(SweetBerryBushBlock.AGE) >= 3;
        }
        if (state.is(Blocks.MELON) || state.is(Blocks.PUMPKIN) || block instanceof PumpkinBlock) {
            return true;
        }

        // Column crops: only grown segments above the base planted block
        if (block instanceof SugarCaneBlock || block instanceof CactusBlock || block instanceof BambooStalkBlock) {
            return level.getBlockState(pos.below()).is(block);
        }

        // Generic modded plant-like blocks: only boost if at max age
        if (isPlantLikeBlock(state)) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof IntegerProperty integerProperty && "age".equals(integerProperty.getName())) {
                    int currentValue = state.getValue(integerProperty);
                    int maxValue = integerProperty.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(currentValue);
                    return currentValue >= maxValue;
                }
            }
            return true;
        }

        return false;
    }

    private static boolean isTreeLog(ServerLevel level, BlockPos logPos, BlockState logState) {
        // Quick check: Are natural leaves in immediate vicinity (radius 2)?
        if (hasNaturalLeavesNearby(level, logPos, 2)) {
            return true;
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        BlockPos root = logPos.immutable();
        queue.add(root);
        visited.add(root);

        int checked = 0;
        int maxChecks = 120;

        while (!queue.isEmpty() && checked < maxChecks) {
            BlockPos current = queue.poll();
            checked++;

            if (hasNaturalLeavesNearby(level, current, 2)) {
                return true;
            }

            // Search neighbors: mainly upward (dy=1,2), horizontal (dy=0), and slightly downward (dy=-1)
            for (int dy = -1; dy <= 2; dy++) {
                int maxH = (dy > 0) ? 1 : (dy == 0 ? 1 : 0);
                for (int dx = -maxH; dx <= maxH; dx++) {
                    for (int dz = -maxH; dz <= maxH; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        if (dy == 0 && Math.abs(dx) + Math.abs(dz) > 1) {
                            continue;
                        }

                        BlockPos next = current.offset(dx, dy, dz);
                        if (!visited.add(next)) {
                            continue;
                        }

                        if (Math.abs(next.getX() - logPos.getX()) > 8 || Math.abs(next.getZ() - logPos.getZ()) > 8) {
                            continue;
                        }
                        if (next.getY() < logPos.getY() - 4 || next.getY() > logPos.getY() + 32) {
                            continue;
                        }

                        BlockState nextState = level.getBlockState(next);
                        if (isNaturalLeaves(nextState)) {
                            return true;
                        }

                        if (nextState.is(BlockTags.LOGS) || nextState.is(logState.getBlock())) {
                            queue.add(next);
                        } else if (nextState.isAir() && dy > 0 && checked < 30) {
                            queue.add(next);
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean hasNaturalLeavesNearby(ServerLevel level, BlockPos center, int radius) {
        for (BlockPos candidate : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(candidate);
            if (isNaturalLeaves(state)) {
                return true;
            }
        }
        return false;
    }

    private static boolean applyBonemealable(ServerLevel level, BlockPos pos, BlockState state, BonemealableBlock bonemealableBlock) {
        BlockState before = state;
        if (bonemealableBlock.isValidBonemealTarget(level, pos, state)) {
            bonemealableBlock.performBonemeal(level, level.random, pos, state);
            return !level.getBlockState(pos).equals(before);
        }

        return false;
    }

    private static boolean tryAdvanceGrowthState(ServerLevel level, BlockPos pos, BlockState state) {
        if (!isPlantLikeBlock(state)) {
            return false;
        }
        if (state.getBlock() instanceof SugarCaneBlock || state.getBlock() instanceof CactusBlock || state.getBlock() instanceof BambooStalkBlock) {
            return false;
        }
        if (state.getBlock() instanceof ChorusFlowerBlock) {
            return false;
        }

        BlockState updatedState = state;
        boolean changed = false;

        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty integerProperty && isSupportedGrowthProperty(state, integerProperty)) {
                int currentValue = updatedState.getValue(integerProperty);
                int maxValue = integerProperty.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(currentValue);
                if (currentValue < maxValue) {
                    int nextValue = Math.min(currentValue + 1, maxValue);
                    updatedState = updatedState.setValue(integerProperty, nextValue);
                    changed = true;
                }
            }
        }

        if (changed) {
            level.setBlock(pos, updatedState, Block.UPDATE_ALL);
        }
        return changed;
    }

    private static boolean trySpecialBonemealGrowth(ServerLevel level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof SugarCaneBlock) {
            return growSugarCaneColumn(level, pos, (SugarCaneBlock) block);
        }
        if (block instanceof CactusBlock) {
            return growCactusColumn(level, pos, (CactusBlock) block);
        }

        return false;
    }

    private static boolean growSugarCaneColumn(ServerLevel level, BlockPos pos, SugarCaneBlock block) {
        BlockPos topPos = findColumnTop(level, pos, block);
        BlockPos growPos = topPos.above();
        if (getColumnHeight(level, topPos, block) >= 3 || !level.isEmptyBlock(growPos)) {
            return false;
        }

        BlockState newState = block.defaultBlockState();
        BlockState topState = level.getBlockState(topPos);
        level.setBlockAndUpdate(growPos, newState);
        level.setBlock(topPos, topState.setValue(SugarCaneBlock.AGE, Integer.valueOf(0)), 4);
        CommonHooks.fireCropGrowPost(level, growPos, newState);
        return true;
    }

    private static boolean growCactusColumn(ServerLevel level, BlockPos pos, CactusBlock block) {
        BlockPos topPos = findColumnTop(level, pos, block);
        BlockPos growPos = topPos.above();
        if (getColumnHeight(level, topPos, block) >= 3 || !level.isEmptyBlock(growPos)) {
            return false;
        }

        BlockState topState = level.getBlockState(topPos);
        BlockState resetState = topState.setValue(CactusBlock.AGE, Integer.valueOf(0));
        level.setBlockAndUpdate(growPos, block.defaultBlockState());
        level.setBlock(topPos, resetState, 4);
        level.neighborChanged(resetState, growPos, block, topPos, false);
        CommonHooks.fireCropGrowPost(level, topPos, topState);
        return true;
    }

    private static BlockPos findColumnTop(BlockGetter level, BlockPos startPos, Block block) {
        BlockPos currentPos = startPos;
        int scannedBlocks = 0;

        while (scannedBlocks < 15 && level.getBlockState(currentPos.above()).is(block)) {
            currentPos = currentPos.above();
            scannedBlocks++;
        }

        return currentPos;
    }

    private static int getColumnHeight(BlockGetter level, BlockPos topPos, Block block) {
        int height = 1;
        while (height < 16 && level.getBlockState(topPos.below(height)).is(block)) {
            height++;
        }
        return height;
    }

    private static boolean isSupportedGrowthProperty(BlockState state, IntegerProperty property) {
        String propertyName = property.getName();
        if ("age".equals(propertyName)) {
            return true;
        }

        return "stage".equals(propertyName) && state.getBlock() instanceof SaplingBlock;
    }

    private static FeaturePlacementPlan createFeaturePlacementPlan(
            ServerLevel level,
            BlockPos eventPos,
            Holder<ConfiguredFeature<?, ?>> feature
    ) {
        BlockState state = level.getBlockState(eventPos);
        if (state.getBlock() instanceof SaplingBlock && isMegaTreeFeature(feature)) {
            for (int xOffset = 0; xOffset >= -1; xOffset--) {
                for (int zOffset = 0; zOffset >= -1; zOffset--) {
                    if (isTwoByTwoSapling(state, level, eventPos, xOffset, zOffset)) {
                        BlockPos root = eventPos.offset(xOffset, 0, zOffset);
                        Map<BlockPos, BlockState> sourceStates = new LinkedHashMap<>();
                        sourceStates.put(root, level.getBlockState(root));
                        sourceStates.put(root.east(), level.getBlockState(root.east()));
                        sourceStates.put(root.south(), level.getBlockState(root.south()));
                        sourceStates.put(root.south().east(), level.getBlockState(root.south().east()));
                        return new FeaturePlacementPlan(root, sourceStates);
                    }
                }
            }
        }

        Map<BlockPos, BlockState> sourceStates = new LinkedHashMap<>();
        sourceStates.put(eventPos.immutable(), state);
        return new FeaturePlacementPlan(eventPos.immutable(), sourceStates);
    }

    private static boolean isMegaTreeFeature(Holder<ConfiguredFeature<?, ?>> featureHolder) {
        if (featureHolder == null) {
            return false;
        }
        ConfiguredFeature<?, ?> configuredFeature = featureHolder.value();
        if (configuredFeature.config() instanceof TreeConfiguration treeConfig) {
            TrunkPlacer trunkPlacer = treeConfig.trunkPlacer;
            return trunkPlacer instanceof GiantTrunkPlacer || trunkPlacer instanceof DarkOakTrunkPlacer;
        }
        return false;
    }

    private static Map<BlockPos, BlockState> removeFeatureSources(ServerLevel level, Map<BlockPos, BlockState> sourceStates) {
        Map<BlockPos, BlockState> replacementStates = new LinkedHashMap<>();
        for (BlockPos sourcePos : sourceStates.keySet()) {
            BlockState replacementState = level.getFluidState(sourcePos).createLegacyBlock();
            replacementStates.put(sourcePos, replacementState);
            level.setBlock(sourcePos, replacementState, 4);
        }
        return replacementStates;
    }

    private static void restoreFeatureSources(ServerLevel level, Map<BlockPos, BlockState> sourceStates) {
        for (Map.Entry<BlockPos, BlockState> entry : sourceStates.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_ALL);
        }
    }

    private static void syncSourceRemovals(ServerLevel level, Map<BlockPos, BlockState> sourceStates, Map<BlockPos, BlockState> replacementStates) {
        for (Map.Entry<BlockPos, BlockState> entry : sourceStates.entrySet()) {
            BlockPos sourcePos = entry.getKey();
            BlockState replacementState = replacementStates.get(sourcePos);
            if (level.getBlockState(sourcePos).equals(replacementState)) {
                level.sendBlockUpdated(sourcePos, entry.getValue(), replacementState, 2);
            }
        }
    }

    private static Set<BlockPos> collectIgnoredFeatureObstacles(ServerLevel level, BlockPos origin, Set<BlockPos> sourcePositions) {
        int radius = GrowAnywayConfig.SERVER.featureClearRadius.get();
        int height = GrowAnywayConfig.SERVER.featureClearHeight.get();
        Set<BlockPos> ignored = new LinkedHashSet<>();

        for (int dy = 0; dy <= height; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos candidate = origin.offset(dx, dy, dz);
                    if (sourcePositions.contains(candidate)) {
                        continue;
                    }

                    BlockState state = level.getBlockState(candidate);
                    if (isIgnoredFeatureObstacle(state)) {
                        ignored.add(candidate.immutable());
                    }
                }
            }
        }

        return ignored;
    }

    private static boolean isIgnoredFeatureObstacle(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return false;
        }
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES) || state.is(BlockTags.SAPLINGS) || state.is(BlockTags.DIRT)) {
            return false;
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return blockId != null && !"minecraft".equals(blockId.getNamespace());
    }

    private static boolean isTwoByTwoSapling(BlockState state, ServerLevel level, BlockPos pos, int xOffset, int zOffset) {
        Block block = state.getBlock();
        return level.getBlockState(pos.offset(xOffset, 0, zOffset)).is(block)
                && level.getBlockState(pos.offset(xOffset + 1, 0, zOffset)).is(block)
                && level.getBlockState(pos.offset(xOffset, 0, zOffset + 1)).is(block)
                && level.getBlockState(pos.offset(xOffset + 1, 0, zOffset + 1)).is(block);
    }

    private record FeaturePlacementPlan(BlockPos placementOrigin, Map<BlockPos, BlockState> sourceStates) {
    }
}