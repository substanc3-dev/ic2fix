package dev.substanc3.ic2fix.mixin;

import ic2.fabric.Ic2Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Make it possible for us to create instances of an internal "Still" class
@Mixin(Ic2Fluid.Still.class)
public interface StillAccessor {
    @Invoker(value = "<init>", remap = false)
    static Ic2Fluid.Still createStill() {
        throw new UnsupportedOperationException();
    }
}
