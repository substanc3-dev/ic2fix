package dev.substanc3.ic2fix.helper;

import team.reborn.energy.api.EnergyStorage;

// Helper interface to associate an EnergyStorage to all blocks
public interface IEnergyStorageProvider {
    EnergyStorage giveEnergy();
    void setEnergy(EnergyStorage a);
}
