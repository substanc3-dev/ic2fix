package dev.substanc3.ic2fix.mixin;

import ic2.core.item.tool.ItemTreetap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

// Fix a crash when IC2 tries to give an achievement on the client side
@Mixin(ItemTreetap.class)
public class ItemTreetapMixin {
    // Remove the player instance when running on the client to not try giving and achievement
    @ModifyArg(method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target= "Lic2/core/item/tool/ItemTreetap;attemptExtract(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Ljava/util/List;)Z"))
    private PlayerEntity removeplayer(PlayerEntity a, World v1, BlockPos var2, Direction var3, BlockState var4, List<ItemStack> var5)
    {
        if(!v1.isClient)
            return null;
        return a;
    }

}
