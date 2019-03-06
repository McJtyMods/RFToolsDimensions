package mcjty.rftoolsdim.gui;

import mcjty.lib.blocks.GenericBlock;
import mcjty.rftoolsdim.items.manual.GuiRFToolsManual;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {
    public static final String SHIFT_MESSAGE = "<Press Shift>";

    /** This is used to keep track of GUIs that we make*/
    private static int modGuiIndex = 0;
    public static final int GUI_ESSENCE_PAINTER = modGuiIndex++;
    public static final int GUI_DIMLET_WORKBENCH = modGuiIndex++;
    public static final int GUI_MANUAL_DIMENSION = modGuiIndex++;
    public static final int GUI_DIMENSION_EDITOR = modGuiIndex++;
    public static final int GUI_DIMENSION_BUILDER = modGuiIndex++;
    public static final int GUI_DIMENSION_ENSCRIBER = modGuiIndex++;

    @Override
    public Object getServerGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GUI_MANUAL_DIMENSION) {
            return null;
        }

//        if (guiid == RFToolsDim.GUI_MANUAL_MAIN || guiid == RFTools.GUI_TELEPORTPROBE || guiid == RFTools.GUI_ADVANCEDPORTER) {
//            return null;
//        } else if (guiid == RFTools.GUI_REMOTE_STORAGE_ITEM) {
//            return new RemoteStorageItemContainer(entityPlayer);
//        } else if (guiid == RFTools.GUI_MODULAR_STORAGE_ITEM) {
//            return new ModularStorageItemContainer(entityPlayer);
//        } else if (guiid == RFTools.GUI_STORAGE_FILTER) {
//            return new StorageFilterContainer(entityPlayer);
//        }

        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock<?, ?> genericBlock = (GenericBlock<?, ?>) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createServerContainer(entityPlayer, te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GUI_MANUAL_DIMENSION) {
            return new GuiRFToolsManual(GuiRFToolsManual.MANUAL_DIMENSION);
        }
//        if (guiid == RFTools.GUI_MANUAL_MAIN) {
//            return new GuiRFToolsManual(GuiRFToolsManual.MANUAL_MAIN);
//        } else if (guiid == RFTools.GUI_TELEPORTPROBE) {
//            return new GuiTeleportProbe();
//        } else if (guiid == RFTools.GUI_ADVANCEDPORTER) {
//            return new GuiAdvancedPorter();
//        } else if (guiid == RFTools.GUI_REMOTE_STORAGE_ITEM) {
//            return new GuiModularStorage(new RemoteStorageItemContainer(entityPlayer));
//        } else if (guiid == RFTools.GUI_MODULAR_STORAGE_ITEM) {
//            return new GuiModularStorage(new ModularStorageItemContainer(entityPlayer));
//        } else if (guiid == RFTools.GUI_STORAGE_FILTER) {
//            return new GuiStorageFilter(new StorageFilterContainer(entityPlayer));
//        }

        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock<?, ?> genericBlock = (GenericBlock<?, ?>) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createClientGui(entityPlayer, te);
        }
        return null;
    }
}
