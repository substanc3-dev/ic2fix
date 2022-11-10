package dev.substanc3.ic2fix.mixin;

import ic2.core.fluid.EnvFluidHandler;
import ic2.fabric.Ic2Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Utility to be able to manipulate "refs" in IC2 fluids
@Mixin(Ic2Fluid.class)
public interface Ic2FluidAccessor {
    @Accessor(remap = false)
    void setRefs(EnvFluidHandler.FluidRefs refs);
}
