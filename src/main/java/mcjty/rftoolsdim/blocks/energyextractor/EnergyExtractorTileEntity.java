package mcjty.rftoolsdim.blocks.energyextractor;

import mcjty.lib.tileentity.GenericEnergyStorageTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsdim.config.MachineConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class EnergyExtractorTileEntity extends GenericEnergyStorageTileEntity implements ITickable {

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
            long dimensionEnergy = storage.getEnergyLevel(getWorld().provider.getDimension());
            int needed = (int)Math.min(MachineConfiguration.EXTRACTOR_MAXENERGY - energyStored, dimensionEnergy);

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

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = getPos().offset(facing);
            TileEntity te = getWorld().getTileEntity(pos);
            EnumFacing opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                int rfToGive = Math.min(MachineConfiguration.EXTRACTOR_SENDPERTICK, energyStored);
                int received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                energyStored -= storage.extractEnergy(received, false);
                if (energyStored <= 0) {
                    break;
                }
            }
        }
    }
}
