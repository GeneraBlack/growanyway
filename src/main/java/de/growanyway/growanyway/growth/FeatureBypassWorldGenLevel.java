package de.growanyway.growanyway.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

final class FeatureBypassWorldGenLevel implements InvocationHandler {
    private static final BlockState MASKED_AIR = Blocks.AIR.defaultBlockState();

    private final ServerLevel delegate;
    private final Set<BlockPos> maskedPositions;
    private final Map<BlockPos, BlockState> virtualStates = new HashMap<>();

    private FeatureBypassWorldGenLevel(ServerLevel delegate, Set<BlockPos> maskedPositions) {
        this.delegate = delegate;
        this.maskedPositions = maskedPositions;
    }

    static WorldGenLevel create(ServerLevel delegate, Set<BlockPos> maskedPositions) {
        return (WorldGenLevel) Proxy.newProxyInstance(
                WorldGenLevel.class.getClassLoader(),
                new Class<?>[] { WorldGenLevel.class },
                new FeatureBypassWorldGenLevel(delegate, maskedPositions)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (method.getDeclaringClass() == Object.class) {
            return handleObjectMethod(proxy, methodName, args);
        }

        return switch (methodName) {
            case "getBlockState" -> getVisibleState((BlockPos) args[0]);
            case "getFluidState" -> getVisibleState((BlockPos) args[0]).getFluidState();
            case "isStateAtPosition" -> isStateAtPosition((BlockPos) args[0], castPredicate(args[1]));
            case "isFluidAtPosition" -> isFluidAtPosition((BlockPos) args[0], castFluidPredicate(args[1]));
            case "isEmptyBlock" -> getVisibleState((BlockPos) args[0]).isAir();
            case "setBlock" -> setBlock(method, (BlockPos) args[0], (BlockState) args[1], args);
            case "removeBlock", "destroyBlock" -> removeBlock(method, (BlockPos) args[0], args);
            case "getBlockEntity" -> getBlockEntity(args);
            default -> invokeOnDelegate(method, args);
        };
    }

    private Object handleObjectMethod(Object proxy, String methodName, Object[] args) {
        return switch (methodName) {
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> "FeatureBypassWorldGenLevel[delegate=" + delegate.dimension().location() + "]";
            default -> throw new IllegalStateException("Unsupported Object method: " + methodName);
        };
    }

    private Object invokeOnDelegate(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(delegate, args);
        } catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }

    private boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> predicate) {
        return predicate.test(getVisibleState(pos));
    }

    private boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> predicate) {
        return predicate.test(getVisibleState(pos).getFluidState());
    }

    private Object getBlockEntity(Object[] args) {
        BlockPos pos = (BlockPos) args[0];
        if (!isMasked(pos)) {
            return args.length == 1 ? delegate.getBlockEntity(pos) : delegate.getBlockEntity(pos, (BlockEntityType<?>) args[1]);
        }

        return args.length == 1 ? null : Optional.empty();
    }

    private boolean setBlock(Method method, BlockPos pos, BlockState state, Object[] args) throws Throwable {
        if (!isMasked(pos)) {
            return (boolean) invokeOnDelegate(method, args);
        }

        virtualStates.put(pos.immutable(), state);
        return true;
    }

    private boolean removeBlock(Method method, BlockPos pos, Object[] args) throws Throwable {
        if (!isMasked(pos)) {
            return (boolean) invokeOnDelegate(method, args);
        }

        virtualStates.put(pos.immutable(), MASKED_AIR);
        return true;
    }

    private BlockState getVisibleState(BlockPos pos) {
        if (!isMasked(pos)) {
            return delegate.getBlockState(pos);
        }

        return virtualStates.getOrDefault(pos, MASKED_AIR);
    }

    private boolean isMasked(BlockPos pos) {
        return maskedPositions.contains(pos);
    }

    @SuppressWarnings("unchecked")
    private Predicate<BlockState> castPredicate(Object candidate) {
        return (Predicate<BlockState>) candidate;
    }

    @SuppressWarnings("unchecked")
    private Predicate<FluidState> castFluidPredicate(Object candidate) {
        return (Predicate<FluidState>) candidate;
    }
}