package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.api.Infusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimletWorkbenchBlock extends GenericRFToolsBlock<DimletWorkbenchTileEntity, DimletWorkbenchContainer> implements Infusable {

    public DimletWorkbenchBlock() {
        super(Material.iron, DimletWorkbenchTileEntity.class, DimletWorkbenchContainer.class, "dimlet_workbench", true);
        setDefaultState(this.blockState.getBaseState());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiDimletWorkbench.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "With this workbench you can deconstruct dimlets");
            list.add(EnumChatFormatting.WHITE + "into individual parts and also reconstruct new dimlets");
            list.add(EnumChatFormatting.WHITE + "out of these parts.");
            list.add(EnumChatFormatting.YELLOW + "Infusing bonus: increased chance of getting");
            list.add(EnumChatFormatting.YELLOW + "all parts out of the deconstructed dimlet.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @Override
    public int getGuiID() {
        return RFToolsDim.GUI_DIMLET_WORKBENCH;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        this.restoreBlockFromNBT(world, pos, stack);
        if(!world.isRemote && GeneralConfig.manageOwnership) {
            this.setOwner(world, pos, placer);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this);
    }
}
