package dev.substanc3.ic2fix.mixin;

import dev.substanc3.ic2fix.IC2FixMod;
import dev.substanc3.ic2fix.helper.IEnergyStorageProvider;
import ic2.core.ref.Ic2BlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

// We need to register an energy storage for IC2 blocks, so they can be used from the TR system
@Mixin(Ic2BlockEntities.class)
public class Ic2BlockEntitiesMixin {
    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/BiFunction;[Lnet/minecraft/block/Block;)Lnet/minecraft/block/entity/BlockEntityType;", at = @At("TAIL"))
    private static <T extends BlockEntity> void registerEnergy(String par1, BiFunction<BlockPos, BlockState, T> par2, Block[] par3, CallbackInfoReturnable<BlockEntityType<T>> cir)
    {
        var entity = cir.getReturnValue();
        // Save the entity type for later comparisons
        IC2FixMod.IC2_BLOCK_ENTITIES.add(entity);
        // Assign an energy storage with the block
        EnergyStorage.SIDED.registerForBlockEntity((myBlockEntity, dir) -> getEnergyStorage(myBlockEntity), entity);
    }

    // Helper to get the energy storage for a block
    private static <T extends BlockEntity> EnergyStorage getEnergyStorage(T entity)
    {
        var energyStore = ((IEnergyStorageProvider)entity).giveEnergy();
        return energyStore;
    }
}
