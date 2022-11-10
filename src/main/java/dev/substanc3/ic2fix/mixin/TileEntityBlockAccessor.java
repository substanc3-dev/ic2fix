package dev.substanc3.ic2fix.mixin;

import ic2.core.block.TileEntityBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

// Add the necessary shim to notify IC2 blocks of neighbor updates
@Mixin(TileEntityBlock.class)
public interface TileEntityBlockAccessor {
    @Invoker
    void callOnNeighborChange(Block var1, BlockPos var2);

    @Invoker
    void callSetFacing(Direction var1);

    @Accessor(remap = false)
    void setFacing(byte facing);
}
