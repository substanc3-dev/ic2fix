package dev.substanc3.ic2fix.mixin;

import ic2.core.item.reactor.AbstractDamageableReactorComponent;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

// Fix durability on reactor components
@Mixin(AbstractDamageableReactorComponent.class)
public class AbstractDamageableReactorComponentMixin {
    // Properly assign the max durability for IC2 reactor items
    @ModifyVariable(method = "<init>", at=@At(value="HEAD"), argsOnly = true, index = 1)
    private static Item.Settings constructHook(Item.Settings settings, Item.Settings settings2, int var2)
    {
        return settings.maxDamage(var2);
    }

    // Make sure IC2 uses the correct NBT tag for durability
    @ModifyArg(method = "setUse(Lnet/minecraft/item/ItemStack;I)V", at = @At(value="INVOKE",target = "Lnet/minecraft/nbt/NbtCompound;putInt(Ljava/lang/String;I)V"))
    private String modifyTagNameSet(String input)
    {
        return "Damage";
    }

    @ModifyArg(method = "getUse(Lnet/minecraft/item/ItemStack;)I", at = @At(value="INVOKE",target = "Lnet/minecraft/nbt/NbtCompound;getInt(Ljava/lang/String;)I"))
    private String modifyTagNameGet(String input)
    {
        return "Damage";
    }

    @ModifyArg(method = "incrementUse(Lnet/minecraft/item/ItemStack;)V", at = @At(value="INVOKE",target = "Lnet/minecraft/nbt/NbtCompound;putInt(Ljava/lang/String;I)V"))
    private String modifyTagNameIncrement(String input)
    {
        return "Damage";
    }
}
