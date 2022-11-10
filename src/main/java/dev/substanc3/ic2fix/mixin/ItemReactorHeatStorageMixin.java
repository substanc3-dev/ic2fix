package dev.substanc3.ic2fix.mixin;

import ic2.core.item.reactor.ItemReactorHeatStorage;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Fix tooltips in some items showing the untranslated string name
@Mixin(ItemReactorHeatStorage.class)
public class ItemReactorHeatStorageMixin {
    @Redirect(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private MutableText fixLiteralToTranslatable(String string)
    {
        return Text.translatable(string);
    }
}
