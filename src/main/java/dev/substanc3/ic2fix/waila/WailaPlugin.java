package dev.substanc3.ic2fix.waila;

import ic2.core.block.RubberLogBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;

// Adds information about rubber tree sap into waila
public class WailaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(new RubberTreeComponentProvider(), TooltipPosition.BODY, RubberLogBlock.class);
    }
}
