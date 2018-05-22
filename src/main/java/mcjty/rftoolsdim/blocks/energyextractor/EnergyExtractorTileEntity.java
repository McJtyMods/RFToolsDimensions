package mcjty.rftoolsdim.blocks.energyextractor;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import mcjty.lib.tileentity.GenericEnergyProviderTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsdim.config.MachineConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyExtractorTileEntity extends GenericEnergyProviderTileEntity implements ITickable {

    public EnergyExtractorTileEntity() {
        super(MachineConfiguration.EXTRACTOR_MAXENERGY, MachineConfiguration.EXTRACTOR_SENDPERTICK);
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        int energyStored = getEnergyStored();

        if (energyStored < MachineConfiguration.EXTRACTOR_MAXENERGY) {
            // Get energy out of the dimension.
            DimensionStorage storage = DimensionStorage.getDimensionStorage(getWorld());
            int dimensionEnergy = storage.getEnergyLevel(getWorld().provider.getDimension());
            int needed = MachineConfiguration.EXTRACTOR_MAXENERGY - energyStored;
            if (needed > dimensionEnergy) {
                needed = dimensionEnergy;
            }

            if (needed > 0) {
                energyStored += needed;
                dimensionEnergy -= needed;
                modifyEnergyStored(needed);

                storage.setEnergyLevel(getWorld().provider.getDimension(), dimensionEnergy);
                storage.save();
            }
        }

        if (energyStored <= 0) {
            return;
        }

        int rf = MachineConfiguration.EXTRACTOR_SENDPERTICK;

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = getPos().offset(facing);
            TileEntity te = getWorld().getTileEntity(pos);
            EnumFacing opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te) || (te != null && te.hasCapability(CapabilityEnergy.ENERGY, opposite))) {
                int rfToGive = rf <= energyStored ? rf : energyStored;
                int received;

                if (McJtyLib.redstoneflux && RedstoneFluxCompatibility.isEnergyConnection(te)) {
                    if (RedstoneFluxCompatibility.canConnectEnergy(te, opposite)) {
                        received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                    } else {
                        received = 0;
                    }
                } else {
                    // Forge unit
                    received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                }
                energyStored -= storage.extractEnergy(received, false);
                if (energyStored <= 0) {
                    break;
                }
            }
        }
    }
}
