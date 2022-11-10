package dev.substanc3.ic2fix.helper;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.*;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

// An implementation of TR EnergyStorage for IC2 blocks
public class IC2EnergyStorage extends SnapshotParticipant<Double> implements EnergyStorage, IEnergyEmitter {
    private final BlockEntity owner;
    private final IEnergyTile tile;
    private double pending;
    public IC2EnergyStorage(BlockEntity entity)
    {
        owner = entity;
        if(entity instanceof IEnergySource || entity instanceof IEnergySink)
            tile = null;
        else
            tile = EnergyNet.instance.getTile(entity.getWorld(), entity.getPos());
    }

    // If available, get the IC2 sink
    private IEnergySink getSink()
    {
        IEnergySink sink = null;
        if(owner instanceof IEnergySink)
            sink = (IEnergySink)owner;
        else if(tile != null && tile instanceof IEnergySink)
            sink = (IEnergySink)tile;

        return sink;
    }

    // If available, get the IC2 source
    private IEnergySource getSource()
    {
        IEnergySource source = null;
        if(owner instanceof IEnergySource)
            source = (IEnergySource)owner;
        else if(tile != null && tile instanceof IEnergySource)
            source = (IEnergySource)tile;

        return source;
    }

    // Perform the requested operations onto the IC2 block after the transaction is committed
    @Override
    protected void onFinalCommit() {
        // If we are looking to provide energy, we need a sink
        if(pending > 0)
        {
            var sink = getSink();

            if(sink != null)
            {
                pending = sink.injectEnergy(Direction.DOWN, pending, 0);
            }
        }
        // If we are looking to draw energy, we need a source
        else if(pending < 0)
        {
            var source = getSource();

            if(source != null)
            {
                source.drawEnergy(-pending);
                pending = 0;
            }
        }
    }

    // Try to insert TR energy into the IC2 block
    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        // Get the sink, if we have one
        var sink = getSink();

        // Calculate the max amount of energy we can insert into this block
        var inserted = Math.min(getCapacity(), maxAmount);
        if(sink == null)
            inserted = Math.min(inserted, (long)-pending);

        if(inserted > 0)
        {
            // Set up the transaction and add the energy to our buffer
            updateSnapshots(transaction);
            pending += inserted;
            return inserted;
        }
        return 0;
    }

    // Try to extract TR energy from the IC2 block
    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        // Get the source, if we have one
        var source = getSource();

        // Calculate the max amount of energy we can extract
        var extracted = Math.min(getAmount(), maxAmount);
        if(source == null)
            extracted = Math.min(extracted, (long)pending);

        if(extracted > 0)
        {
            // Set up the transaction and take the energy from our buffer
            updateSnapshots(transaction);
            pending -= extracted;
            return extracted;
        }
        return 0;
    }

    // Get the amount of energy we might be able to provide
    @Override
    public long getAmount() {
        var source = getSource();

        if(source != null)
        {
            return (long)(source.getOfferedEnergy() + pending);
        }
        return 0;
    }

    // Get the amount of energy we may be able to accept
    @Override
    public long getCapacity() {
        var sink = getSink();

        if(sink != null)
        {
            return (long)(sink.getDemandedEnergy() - pending);
        }
        return 0;
    }

    @Override
    public boolean supportsInsertion() {
        return owner instanceof IEnergySink || (tile != null && tile instanceof IEnergySink);
    }

    @Override
    public boolean supportsExtraction() {
        return owner instanceof IEnergySource || (tile != null && tile instanceof IEnergySource);
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, Direction direction) {
        return true;
    }

    @Override
    protected Double createSnapshot() {
        return pending;
    }

    @Override
    protected void readSnapshot(Double snapshot) {
        pending = snapshot;
    }
}
