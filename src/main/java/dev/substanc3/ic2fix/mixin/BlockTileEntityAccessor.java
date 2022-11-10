package dev.substanc3.ic2fix.mixin;

import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Helper to be able to easily get the block entity for a given block
@Mixin(BlockTileEntity.class)
public interface BlockTileEntityAccessor {
    @Invoker
    static TileEntityBlock callGetTe(BlockView var0, BlockPos var1) {
        throw new UnsupportedOperationException();
    }
}
