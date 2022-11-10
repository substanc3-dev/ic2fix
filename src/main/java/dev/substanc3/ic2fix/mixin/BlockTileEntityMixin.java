package dev.substanc3.ic2fix.mixin;

import ic2.core.block.BlockTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockTileEntity.class)
@SuppressWarnings({"deprecation"})
public abstract class BlockTileEntityMixin extends AbstractBlock {

    public BlockTileEntityMixin(Settings settings) {
        super(settings);
    }

    // Injecting block updates into TileEntities
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        var myTe = BlockTileEntityAccessor.callGetTe(world, pos);
        if(myTe != null)
        {
            ((TileEntityBlockAccessor)myTe).callOnNeighborChange(sourceBlock, sourcePos);
        }
    }
}
