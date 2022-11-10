package dev.substanc3.ic2fix.helper;

import ic2.api.energy.tile.*;
import ic2.api.info.ILocatable;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

// An implementation of IC2 IEnergyTile for TR Energy blocks
public class EnergyStorageIC2Tile implements ILocatable, IEnergyTile, IEnergySource, IEnergySink {
    private final World world;
    private final BlockPos pos;
    private EnergyStorage cachedSink = null;
    private EnergyStorage cachedSource = null;
    private boolean initialized = false;

    public EnergyStorageIC2Tile(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    private EnergyStorage getEnergyStorage(boolean wantSink)
    {
        // Only initialize our EnergyStorage caches once
        if(!initialized)
        {
            // Search every direction for a potential sink/source
            for (Direction side : Direction.values()) {
                var test = EnergyStorage.SIDED.find(world, pos, side);
                if(test == null || test instanceof IC2EnergyStorage)
                    continue;
                if(cachedSource == null && test.supportsExtraction())
                    cachedSource = test;
                if(cachedSink == null && test.supportsInsertion())
                    cachedSink = test;
                if(cachedSource != null && cachedSink != null)
                    break;
            }

            initialized = true;
        }

        return wantSink ? cachedSink : cachedSource;
    }

    // Gets the amount of energy we might be able to accept
    @Override
    public double getDemandedEnergy() {
        var es = getEnergyStorage(true);
        if(es == null)
            return 0;
        try(Transaction a = Transaction.openOuter())
        {
            var cap = es.insert(8192, a);
            return cap;
        }
    }

    // Tries to inject energy into the TR block from an IC2 Enet
    @Override
    public double injectEnergy(Direction direction, double v, double v1) {
        try(Transaction a = Transaction.openOuter())
        {
            var es = getEnergyStorage(true);
            if(es == null)
                return 0;
            var done = es.insert((long)v, a);
            a.commit();
            return v - done;
        }
    }

    // Gets the amount of energy we might be able to provide
    @Override
    public double getOfferedEnergy() {
        var es = getEnergyStorage(false);
        if(es == null)
            return 0;
        try(Transaction a = Transaction.openOuter())
        {
            var cap = es.extract(8192, a);
            return cap;
        }
    }

    // Tries to take energy from the TR block into an IC2 block
    @Override
    public void drawEnergy(double v) {
        try(Transaction a = Transaction.openNested(null))
        {
            var es = getEnergyStorage(false);
            if(es == null)
                return;
            es.extract((long)v, a);
            a.commit();
        }
    }

    @Override
    public int getSourceTier() {
        return 0;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, Direction direction) {
        return true;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public World getWorldObj() {
        return world;
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, Direction direction) {
        return true;
    }

    @Override
    public int getSinkTier() {
        return 6;
    }
}
