package dev.substanc3.ic2fix.mixin;

import dev.substanc3.ic2fix.IC2FixMod;
import dev.substanc3.ic2fix.helper.EnergyStorageIC2Tile;
import dev.substanc3.ic2fix.helper.IC2EnergyStorage;
import dev.substanc3.ic2fix.helper.IEnergyStorageProvider;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.reborn.energy.api.EnergyStorage;

// Helper mixin to set up IC2 Enet and TR Energy interop
@Mixin(BlockEntity.class)
public class BlockEntityMixin implements IEnergyStorageProvider {
    // Helper variable/functions to assign an EnergyStorage instance to each block
    private EnergyStorage energyStorage;
    @Override
    public EnergyStorage giveEnergy() {
        return energyStorage;
    }
    @Override
    public void setEnergy(EnergyStorage a) {
        energyStorage = a;
    }

    // Inject a TR EnergyStorage to blocks that directly implement the IC2 IEnergy* interfaces
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectEnergyStorage(BlockEntityType type, BlockPos pos, BlockState state, CallbackInfo ci)
    {
        var entity = (BlockEntity)(Object)this;
        if(entity instanceof IEnergySource || entity instanceof IEnergySink)
            energyStorage = new IC2EnergyStorage(entity);
        else
            energyStorage = null;
    }

    // Inject an IC2 IEnergyTile to blocks that implement the TR Energy API
    @Inject(method = "setWorld(Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    private void setWorldHook(World world, CallbackInfo ci)
    {
        var entity = (BlockEntity)(Object)this;

        // Make sure we don't try to make a tile for IC2 blocks
        if(world.isClient || IC2FixMod.IC2_BLOCK_ENTITIES.contains(entity.getType()))
            return;

        var pos = entity.getPos();

        // Search for a TR EnergyStorage
        boolean any = false;
        for (Direction side : Direction.values()) {
            if(EnergyStorage.SIDED.find(world, pos, entity.getCachedState(), entity, side) != null)
            {
                any = true;
                break;
            }
        }

        // If we found one, create an IC2 IEnergyTile
        if(any)
        {
            EnergyNet.instance.addLocatableTile(new EnergyStorageIC2Tile(world, pos));
        }
    }

    // Make sure we destroy the IC2 IEnergyTile when the block is destroyed
    @Inject(method = "markRemoved()V", at=@At("TAIL"))
    private void destructor(CallbackInfo ci)
    {
        var entity = (BlockEntity)(Object)this;

        // Make sure we are on the server
        if(entity.getWorld() == null || entity.getWorld().isClient)
            return;

        // Find and remove the tile
        var tile = EnergyNet.instance.getTile(entity.getWorld(), entity.getPos());
        if(tile instanceof EnergyStorageIC2Tile)
        {
            EnergyNet.instance.removeTile(tile);
        }
    }
}
