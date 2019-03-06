package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MaterialAbsorberBlock extends GenericRFToolsBlock<MaterialAbsorberTileEntity, EmptyContainer> {

    public MaterialAbsorberBlock() {
        super(Material.IRON, MaterialAbsorberTileEntity.class, EmptyContainer::new, "material_absorber", false);
    }

    @Override
    public RotationType getRotationType() {
        return  RotationType.NONE;
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof MaterialAbsorberTileEntity) {
            MaterialAbsorberTileEntity tileEntity = (MaterialAbsorberTileEntity) te;
            if (tileEntity.getBlockState() != null) {
                Block block = tileEntity.getBlockState().getBlock();
                int meta = block.getMetaFromState(tileEntity.getBlockState());
                int absorbing = tileEntity.getAbsorbing();
                int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                ItemStack stack = new ItemStack(block, 1, meta);
                probeInfo.text(TextFormatting.GREEN + "Block: " + stack.getDisplayName())
                        .horizontal()
                            .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"))
                            .item(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        MaterialAbsorberTileEntity tileEntity = (MaterialAbsorberTileEntity) accessor.getTileEntity();
        if (tileEntity != null && tileEntity.getBlockState() != null) {
            Block block = tileEntity.getBlockState().getBlock();
            int meta = block.getMetaFromState(tileEntity.getBlockState());
            int absorbing = tileEntity.getAbsorbing();
            int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
            currenttip.add(TextFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName() + " (" + pct + "%)");
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("block")) {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(tagCompound.getString("block")));
            if (block != null) {
                int meta = tagCompound.getInteger("meta");
                if (Item.getItemFromBlock(block) == null) {
                    list.add(TextFormatting.RED + "Block: ERROR");
                } else {
                    list.add(TextFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName());
                }
                int absorbing = tagCompound.getInteger("absorbing");
                int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                list.add(TextFormatting.GREEN + "Absorbed: " + pct + "%");
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Place this block on top of another block and it will");
            list.add(TextFormatting.WHITE + "gradually absorb all identical blocks in the area.");
            list.add(TextFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
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
