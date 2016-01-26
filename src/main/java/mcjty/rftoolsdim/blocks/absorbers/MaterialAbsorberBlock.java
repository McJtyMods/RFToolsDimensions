package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MaterialAbsorberBlock extends GenericRFToolsBlock<MaterialAbsorberTileEntity, EmptyContainer> {

    public MaterialAbsorberBlock() {
        super(Material.iron, MaterialAbsorberTileEntity.class, EmptyContainer.class, "material_absorber", false);
    }

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        MaterialAbsorberTileEntity tileEntity = (MaterialAbsorberTileEntity) accessor.getTileEntity();
        if (tileEntity != null && tileEntity.getBlockState() != null) {
            Block block = tileEntity.getBlockState().getBlock();
            int meta = block.getMetaFromState(tileEntity.getBlockState());
            int absorbing = tileEntity.getAbsorbing();
            int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
            currenttip.add(EnumChatFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName() + " (" + pct + "%)");
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("block")) {
            Block block = Block.blockRegistry.getObject(new ResourceLocation(tagCompound.getString("block")));
            if (block != null) {
                int meta = tagCompound.getInteger("meta");
                list.add(EnumChatFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName());
                int absorbing = tagCompound.getInteger("absorbing");
                int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                list.add(EnumChatFormatting.GREEN + "Absorbed: " + pct + "%");
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

    @SideOnly(Side.CLIENT)
    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
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
}
