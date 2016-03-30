package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class LiquidAbsorberBlock extends GenericRFToolsBlock<LiquidAbsorberTileEntity, EmptyContainer> {

    public LiquidAbsorberBlock() {
        super(Material.iron, LiquidAbsorberTileEntity.class, EmptyContainer.class, "liquid_absorber", false);
    }

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    //@todo
//    @SideOnly(Side.CLIENT)
//    @Override
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        super.getWailaBody(itemStack, currenttip, accessor, config);
//        LiquidAbsorberTileEntity tileEntity = (LiquidAbsorberTileEntity) accessor.getTileEntity();
//        if (tileEntity != null && tileEntity.getBlock() != null) {
//            Block block = tileEntity.getBlock();
//            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
//            if (fluid != null) {
//                int absorbing = tileEntity.getAbsorbing();
//                int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
//                currenttip.add(TextFormatting.GREEN + "Liquid: " + new FluidStack(fluid, 1).getLocalizedName() + " (" + pct + "%)");
//            }
//        }
//        return currenttip;
//    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("liquid")) {
            Block block = Block.blockRegistry.getObject(new ResourceLocation(tagCompound.getString("block")));
            if (block != null) {
                Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                if (fluid != null) {
                    list.add(TextFormatting.GREEN + "Liquid: " + new FluidStack(fluid, 1).getLocalizedName());
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxLiquidAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxLiquidAbsorbtion;
                    list.add(TextFormatting.GREEN + "Absorbed: " + pct + "%");
                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Place this block on top of a liquid and it will");
            list.add(TextFormatting.WHITE + "gradually absorb all this liquid in the area.");
            list.add(TextFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(TextFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int getGuiID() {
        return -1;
    }
}
