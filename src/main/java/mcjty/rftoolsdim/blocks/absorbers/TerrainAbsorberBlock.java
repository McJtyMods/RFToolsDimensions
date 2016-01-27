package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class TerrainAbsorberBlock extends GenericRFToolsBlock<TerrainAbsorberTileEntity, EmptyContainer> {

    public TerrainAbsorberBlock() {
        super(Material.iron, TerrainAbsorberTileEntity.class, EmptyContainer.class, "terrain_absorber", false);
    }

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        TerrainAbsorberTileEntity tileEntity = (TerrainAbsorberTileEntity) accessor.getTileEntity();
        if (tileEntity != null && tileEntity.getTerrainName() != null) {
            String terrainName = tileEntity.getTerrainName();
            int absorbing = tileEntity.getAbsorbing();
            int pct = ((DimletConstructionConfiguration.maxTerrainAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxTerrainAbsorbtion;
            currenttip.add(EnumChatFormatting.GREEN + "Terrain: " + terrainName + " (" + pct + "%)");
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("terrain")) {
            String terrainName = tagCompound.getString("terrain");
            list.add(EnumChatFormatting.GREEN + "Terrain: " + terrainName);
            int absorbing = tagCompound.getInteger("absorbing");
            int pct = ((DimletConstructionConfiguration.maxTerrainAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxTerrainAbsorbtion;
            list.add(EnumChatFormatting.GREEN + "Absorbed: " + pct + "%");
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "Place this block in an area and it will");
            list.add(EnumChatFormatting.WHITE + "gradually absorb the essence of the terrain it is in.");
            list.add(EnumChatFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        // We don't want what GenericBlock does.
        restoreBlockFromNBT(world, pos, itemStack);
        if (!world.isRemote) {
            TerrainAbsorberTileEntity tileEntity = (TerrainAbsorberTileEntity) world.getTileEntity(pos);
            tileEntity.placeDown();
        }
        setOwner(world, pos, entityLivingBase);
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
