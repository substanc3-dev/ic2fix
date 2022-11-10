package dev.substanc3.ic2fix.mixin;

import dev.substanc3.ic2fix.helper.EnergyStorageIC2Tile;
import dev.substanc3.ic2fix.helper.IC2EnergyStorage;
import dev.substanc3.ic2fix.helper.IEnergyStorageProvider;
import ic2.api.energy.EnergyNet;
import ic2.core.block.TileEntityBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

@Mixin(TileEntityBlock.class)
public abstract class TileEntityBlockMixin {
    @Shadow public abstract Direction getFacing();

    // Try to push energy from generators into the TR energy system every tick
    @Inject(method = "tick()V", at=@At("TAIL"), remap = false)
    private void pushEnet(CallbackInfo ci)
    {
        var entity = (TileEntityBlock)(Object)this;
        // Look for TR energy blocks in every direction
        for (Direction side : Direction.values()) {
            var target = EnergyStorage.SIDED.find(entity.getWorld(), entity.getPos().offset(side), side.getOpposite());

            // Make sure we don't try to push into another IC2 block through the TR system
            if(target == null || target instanceof IC2EnergyStorage)
                continue;

            // Get our energy store and try to move energy from it
            var energyStore = ((IEnergyStorageProvider)this).giveEnergy();
            EnergyStorageUtil.move(
                    energyStore,
                    target,
                    Long.MAX_VALUE,
                    null
            );
        }
    }

    // Create an energy storage for blocks that don't directly implement IEnergy* but instead have an IEnergyTile
    @Inject(method = "onLoaded()V", at=@At("TAIL"), remap = false)
    private void loadHook(CallbackInfo ci)
    {
        var energyStoreManager = (IEnergyStorageProvider)this;
        var entity = (BlockEntity)(Object)this;
        var world = entity.getWorld();
        var pos = entity.getPos();
        // Check if we need to create an energy storage
        if(energyStoreManager.giveEnergy() == null && world != null && !world.isClient && EnergyNet.instance.getTile(world, pos) != null)
        {
            // Make sure we don't place our TR compatibility shims into the TR energy system
            if(EnergyNet.instance.getTile(world, pos) instanceof EnergyStorageIC2Tile)
                return;

            energyStoreManager.setEnergy(new IC2EnergyStorage(entity));
        }
    }

    // Make sure a proper facing update is always propagated on placement
    @Inject(method="onPlaced(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/Direction;)V", at=@At("TAIL"))
    private void placedHook(ItemStack par1, LivingEntity par2, Direction par3, CallbackInfo ci)
    {
        var teb = ((TileEntityBlockAccessor)this);
        var facing = this.getFacing();
        teb.setFacing((byte)facing.getOpposite().ordinal());
        teb.callSetFacing(facing);
    }
}
