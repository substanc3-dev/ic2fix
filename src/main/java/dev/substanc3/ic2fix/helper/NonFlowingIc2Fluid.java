package dev.substanc3.ic2fix.helper;

import dev.substanc3.ic2fix.mixin.Ic2FluidAccessor;
import ic2.core.fluid.EnvFluidHandler;
import ic2.fabric.Ic2Fluid;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

// Helper class to implement an IC2 fluid that doesn't flow
public class NonFlowingIc2Fluid extends Ic2Fluid {
    public NonFlowingIc2Fluid(EnvFluidHandler.FluidRefs refs)
    {
        ((Ic2FluidAccessor)this).setRefs(refs);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Override
    public boolean isStill(FluidState state) {
        return true;
    }

    @Override
    public int getLevel(FluidState state) {
        return 8;
    }

    @Override
    protected void tryFlow(WorldAccess world, BlockPos fluidPos, FluidState state) {
    }

    @Override
    public int getTickRate(WorldView world) {
        return 0;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 0;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 0;
    }
}
