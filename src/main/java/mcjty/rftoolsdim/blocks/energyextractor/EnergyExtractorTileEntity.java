package mcjty.rftoolsdim.blocks.energyextractor;

import cofh.api.energy.IEnergyConnection;
import mcjty.lib.entity.GenericEnergyProviderTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsdim.config.MachineConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

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
        int energyStored = getEnergyStored(EnumFacing.DOWN);

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
                storage.save(getWorld());
            }
        }

        if (energyStored <= 0) {
            return;
        }

        int rf = MachineConfiguration.EXTRACTOR_SENDPERTICK;

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = getPos().offset(facing);
            TileEntity te = getWorld().getTileEntity(pos);
            if (EnergyTools.isEnergyTE(te)) {
                EnumFacing opposite = facing.getOpposite();
                int rfToGive = rf <= energyStored ? rf : energyStored;
                int received;

                if (te instanceof IEnergyConnection) {
                    IEnergyConnection connection = (IEnergyConnection) te;
                    if (connection.canConnectEnergy(opposite)) {
                        received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                    } else {
                        received = 0;
                    }
                } else {
                    // Forge unit
                    received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                }
                energyStored -= storage.extractEnergy(received, false);
                if (energyStored <= 0) {
                    break;
                }
            }
        }
    }
}
