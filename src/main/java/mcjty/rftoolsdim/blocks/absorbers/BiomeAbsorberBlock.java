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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BiomeAbsorberBlock extends GenericRFToolsBlock<BiomeAbsorberTileEntity, EmptyContainer> {

    public BiomeAbsorberBlock() {
        super(Material.IRON, BiomeAbsorberTileEntity.class, EmptyContainer::new, "biome_absorber", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof BiomeAbsorberTileEntity) {
            BiomeAbsorberTileEntity tileEntity = (BiomeAbsorberTileEntity) te;
            if (tileEntity.getBiomeName() != null) {
                String biomeName = tileEntity.getBiomeName();
                int absorbing = tileEntity.getAbsorbing();
                int pct = ((DimletConstructionConfiguration.maxBiomeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBiomeAbsorbtion;
                probeInfo.text(TextFormatting.GREEN + "Biome: " + biomeName)
                    .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        BiomeAbsorberTileEntity tileEntity = (BiomeAbsorberTileEntity) accessor.getTileEntity();
        if (tileEntity != null && tileEntity.getBiomeName() != null) {
            String biomeName = tileEntity.getBiomeName();
            int absorbing = tileEntity.getAbsorbing();
            int pct = ((DimletConstructionConfiguration.maxBiomeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBiomeAbsorbtion;
            currenttip.add(TextFormatting.GREEN + "Biome: " + biomeName + " (" + pct + "%)");
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("biome")) {
            String biomeId = tagCompound.getString("biome");
            String biomeName = BiomeAbsorberTileEntity.getBiomeName(biomeId);
            list.add(TextFormatting.GREEN + "Biome: " + biomeName);
            int absorbing = tagCompound.getInteger("absorbing");
            int pct = ((DimletConstructionConfiguration.maxBiomeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBiomeAbsorbtion;
            list.add(TextFormatting.GREEN + "Absorbed: " + pct + "%");
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Place this block in an area and it will");
            list.add(TextFormatting.WHITE + "gradually absorb the essence of the biome it is in.");
            list.add(TextFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        // We don't want what GenericBlock does.
        restoreBlockFromNBT(world, pos, itemStack);
        if (!world.isRemote) {
            BiomeAbsorberTileEntity biomeAbsorberTileEntity = (BiomeAbsorberTileEntity) world.getTileEntity(pos);
            biomeAbsorberTileEntity.placeDown();
        }
        setOwner(world, pos, entityLivingBase);
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
