package dev.substanc3.ic2fix.mixin;

import ic2.core.item.armor.ItemArmorFluidTank;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

// Fix some IC2 items showing untranslated names of fluids
@Mixin(ItemArmorFluidTank.class)
public class ItemArmorFluidTankMixin {
    @ModifyArgs(method = "getContentDescription(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", at = @At(value="INVOKE", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private static void textFix(Args args)
    {
        Object[] a = args.get(1);
        String str = "fluids." + ((Identifier)a[0]).toString().replace(':', '.');
        a[0] = Text.translatable(str).getString();
        args.set(1, a);
    }
}
