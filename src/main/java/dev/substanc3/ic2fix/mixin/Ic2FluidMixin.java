package dev.substanc3.ic2fix.mixin;

import ic2.core.fluid.EnvFluidHandler;
import ic2.fabric.Ic2Fluid;
import dev.substanc3.ic2fix.helper.NonFlowingIc2Fluid;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Fixes the creation of IC2 Fluids
@Mixin(Ic2Fluid.class)
public class Ic2FluidMixin {
	private static Ic2Fluid.Still saved = null;

	// We need to hook the initialization of Block in create, as we create our own FluidBlock later,
	// and we don't want to leave a hanging unregistered Block reference
	@Redirect(at = @At(value="NEW", target="(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;"), method = "Lic2/fabric/Ic2Fluid;create(Lnet/minecraft/block/Material;IIIIZ)Lic2/core/fluid/EnvFluidHandler$FluidRefs;")
	private static Block initBlock(AbstractBlock.Settings settings) {
		return Blocks.AIR;
	}

	// We need to hook the initialization of Ic2Fluid.Still in create, as we create our own NonFlowingIc2Fluid later,
	// and we don't want to leave a hanging unregistered Ic2Fluid.Still reference
	@Redirect(at = @At(value="NEW", target="()Lic2/fabric/Ic2Fluid$Still;", remap = false), method = "Lic2/fabric/Ic2Fluid;create(Lnet/minecraft/block/Material;IIIIZ)Lic2/core/fluid/EnvFluidHandler$FluidRefs;")
	private static Ic2Fluid.Still initStill(Material var0, int var1, int var2, int var3, int var4, boolean var5) {
		if(var5)
		{
			var inst = StillAccessor.createStill();
			if(saved == null)
				saved = inst;
			return inst;
		}
		return saved;
	}

	// We need to hook IC2 creating the references for use in the fluids, to create our own FluidBlock for them
	// IC2 usually just creates a Block instead of a FluidBlock, making fluids broken
	@Redirect(at = @At(value="NEW", target="(Lnet/minecraft/block/Block;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/item/BucketItem;)Lic2/core/fluid/EnvFluidHandler$FluidRefs;"), method = "Lic2/fabric/Ic2Fluid;create(Lnet/minecraft/block/Material;IIIIZ)Lic2/core/fluid/EnvFluidHandler$FluidRefs;")
	private static EnvFluidHandler.FluidRefs initRefs(Block block, Fluid still, Fluid flowing, BucketItem bucketItem, Material var0) {
		// If the fluid is supposed to flow, we just need to set up dummy refs for FluidBlock instantiation
		if(flowing != null) {
			assert flowing instanceof Ic2Fluid;
			((Ic2FluidAccessor)flowing).setRefs(new EnvFluidHandler.FluidRefs(block, still, flowing, bucketItem));
		}
		else {
			// If the fluid is supposed to just be Still, we need to use our own IC2Fluid implementation
			flowing = new NonFlowingIc2Fluid(new EnvFluidHandler.FluidRefs(block, null, null, bucketItem));
			still = flowing;
		}

		// Set the dummy refs for still as well
		assert still instanceof Ic2Fluid;
		((Ic2FluidAccessor)still).setRefs(new EnvFluidHandler.FluidRefs(block, still, flowing, bucketItem));

		// Set up final refs, with our FluidBlock
		var refs = new EnvFluidHandler.FluidRefs(new FluidBlock((FlowableFluid)still, AbstractBlock.Settings.of(var0)), still, flowing, bucketItem);

		// Set the final refs, and return them
		if(flowing instanceof NonFlowingIc2Fluid)
		{
			((Ic2FluidAccessor)flowing).setRefs(refs);
			return new EnvFluidHandler.FluidRefs(refs.block, still, null, bucketItem);
		}
		else
			return refs;
	}

	// We need to fix Fluid flow by using the LEVEL property, otherwise fluids propagate into infinity
	@Inject(at = @At("RETURN"), method = "Lic2/fabric/Ic2Fluid;toBlockState(Lnet/minecraft/fluid/FluidState;)Lnet/minecraft/block/BlockState;", cancellable = true)
	private void toBlockStateHook(FluidState par1, CallbackInfoReturnable<BlockState> cir)
	{
		cir.setReturnValue(cir.getReturnValue().with(Properties.LEVEL_15, FlowableFluid.getBlockStateLevel(par1)));
	}
}
