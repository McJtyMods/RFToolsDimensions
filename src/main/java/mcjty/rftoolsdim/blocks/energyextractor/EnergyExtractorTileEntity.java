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
        long storedPower = getStoredPower();

        if (storedPower < MachineConfiguration.EXTRACTOR_MAXENERGY) {
            // Get energy out of the dimension.
            DimensionStorage storage = DimensionStorage.getDimensionStorage(getWorld());
            long dimensionEnergy = storage.getEnergyLevel(getWorld().provider.getDimension());
            long needed = Math.min(MachineConfiguration.EXTRACTOR_MAXENERGY - storedPower, dimensionEnergy);

            if (needed > 0) {
                storedPower += needed;
                dimensionEnergy -= needed;
                modifyEnergyStored(needed);

                storage.setEnergyLevel(getWorld().provider.getDimension(), dimensionEnergy);
                storage.save();
            }
        }

        if (storedPower <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = getPos().offset(facing);
            TileEntity te = getWorld().getTileEntity(pos);
            EnumFacing opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                long rfToGive = Math.min(MachineConfiguration.EXTRACTOR_SENDPERTICK, storedPower);
                long received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                storedPower -= storage.extractEnergy(received, false);
                if (storedPower <= 0) {
                    break;
                }
            }
        }
    }
}
