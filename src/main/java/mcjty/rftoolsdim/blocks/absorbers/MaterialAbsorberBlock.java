package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MaterialAbsorberBlock extends GenericRFToolsBlock<MaterialAbsorberTileEntity, EmptyContainer> {

    public MaterialAbsorberBlock() {
        super(Material.iron, MaterialAbsorberTileEntity.class, EmptyContainer.class, "material_absorber", false);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tagCompound = accessor.getNBTData();
        if (tagCompound != null) {
            int blockID = tagCompound.getInteger("block");
            if (blockID != -1) {
                Block block = (Block) Block.blockRegistry.getObjectById(blockID);
                if (block != null) {
                    int meta = tagCompound.getInteger("meta");
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                    currenttip.add(EnumChatFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName() + " (" + pct + "%)");
                }
            }
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            int blockID = tagCompound.getInteger("block");
            if (blockID != -1) {
                Block block = (Block) Block.blockRegistry.getObjectById(blockID);
                if (block != null) {
                    int meta = tagCompound.getInteger("meta");
                    list.add(EnumChatFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName());
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                    list.add(EnumChatFormatting.GREEN + "Absorbed: " + pct + "%");
                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "Place this block on top of another block and it will");
            list.add(EnumChatFormatting.WHITE + "gradually absorb all identical blocks in the area.");
            list.add(EnumChatFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getGuiID() {
        return -1;
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
