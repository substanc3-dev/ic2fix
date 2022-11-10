package dev.substanc3.ic2fix.waila;

import ic2.core.block.RubberLogBlock;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

// Adds information about rubber tree sap into waila
public class RubberTreeComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        // Get the state of the rubber tree log
        var state = accessor.getBlockState().get(RubberLogBlock.stateProperty);

        // Check if it can produce sap
        if(state.isPlain()) {
            tooltip.addLine(Text.literal("Cannot produce sap.").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        }
        else
        {
            // Check if it currently has sap to be harvested
            if(state.wet)
                tooltip.addLine(Text.literal("Ready to harvest sap.").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)));
            else
                tooltip.addLine(Text.literal("Produces sap.").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        }
    }
}
