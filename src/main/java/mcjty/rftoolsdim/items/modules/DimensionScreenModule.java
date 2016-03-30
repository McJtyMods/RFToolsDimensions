package mcjty.rftoolsdim.items.modules;

import mcjty.rftools.api.screens.IScreenDataHelper;
import mcjty.rftools.api.screens.IScreenModule;
import mcjty.rftools.api.screens.data.IModuleDataContents;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class DimensionScreenModule implements IScreenModule<IModuleDataContents> {
    private int dim = 0;
    private ScreenModuleHelper helper = new ScreenModuleHelper();

    @Override
    public IModuleDataContents getData(IScreenDataHelper h, World worldObj, long millis) {
        int energy = DimensionStorage.getDimensionStorage(DimensionManager.getWorld(0)).getEnergyLevel(dim);
        return helper.getContentsValue(h, millis, energy, PowerConfiguration.MAX_DIMENSION_POWER);
    }

    @Override
    public void setupFromNBT(NBTTagCompound tagCompound, int dim, BlockPos pos) {
        if (tagCompound != null) {
            this.dim = tagCompound.getInteger("dim");
            helper.setShowdiff(tagCompound.getBoolean("showdiff"));
        }
    }

    @Override
    public int getRfPerTick() {
        return GeneralConfiguration.DIMENSIONMODULE_RFPERTICK;
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }
}
