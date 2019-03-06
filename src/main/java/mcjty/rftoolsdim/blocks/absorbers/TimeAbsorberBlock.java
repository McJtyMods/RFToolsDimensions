package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class TimeAbsorberBlock extends GenericRFToolsBlock<TimeAbsorberTileEntity, EmptyContainer> {

    public TimeAbsorberBlock() {
        super(Material.IRON, TimeAbsorberTileEntity.class, EmptyContainer::new, "time_absorber", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public boolean needsRedstoneCheck() {
        return true;
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TimeAbsorberTileEntity) {
            TimeAbsorberTileEntity timeAbsorberTileEntity = (TimeAbsorberTileEntity) te;
            float angle = timeAbsorberTileEntity.getAngle();
            if (angle >= -0.01f) {
                DimletKey key = TimeAbsorberTileEntity.findBestTimeDimlet(angle);
                String name = KnownDimletConfiguration.getDisplayName(key);
                if (name == null) {
                    name = "<unknown>";
                }
                int absorbing = timeAbsorberTileEntity.getAbsorbing();
                int pct = ((DimletConstructionConfiguration.maxTimeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxTimeAbsorbtion;
                probeInfo.text(TextFormatting.GREEN + "Time: " + name)
                        .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
            } else {
                probeInfo.text(TextFormatting.GREEN + "Give this block a redstone signal");
                probeInfo.text(TextFormatting.GREEN + "at the right time you want to absorb");
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        TileEntity te = accessor.getTileEntity();
        if (te instanceof TimeAbsorberTileEntity) {
            TimeAbsorberTileEntity timeAbsorberTileEntity = (TimeAbsorberTileEntity) te;
            float angle = timeAbsorberTileEntity.getAngle();
            if (angle >= -0.01f) {
                DimletKey key = TimeAbsorberTileEntity.findBestTimeDimlet(angle);
                String name = KnownDimletConfiguration.getDisplayName(key);
                if (name == null) {
                    name = "<unknown>";
                }
                int absorbing = timeAbsorberTileEntity.getAbsorbing();
                int pct = ((DimletConstructionConfiguration.maxTimeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxTimeAbsorbtion;
                currenttip.add(TextFormatting.GREEN + "Dimlet: " + name + " (" + angle + ", " + pct + "%)");
            } else {
                currenttip.add(TextFormatting.GREEN + "Give this block a redstone signal");
                currenttip.add(TextFormatting.GREEN + "at the right time you want to absorb");
            }
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("angle") && tagCompound.getFloat("angle") > -0.001f) {
                float angle = tagCompound.getFloat("angle");
                DimletKey key = TimeAbsorberTileEntity.findBestTimeDimlet(angle);
                String name = KnownDimletConfiguration.getDisplayName(key);
                if (name == null) {
                    name = "<unknown>";
                }
                list.add(TextFormatting.GREEN + "Dimlet: " + name + " (" + angle + ")");
                int absorbing = tagCompound.getInteger("absorbing");
                int pct = ((DimletConstructionConfiguration.maxTimeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxTimeAbsorbtion;
                list.add(TextFormatting.GREEN + "Absorbed: " + pct + "%");
                int timeout = tagCompound.getInteger("registerTimeout");
                list.add(TextFormatting.GREEN + "Timeout: " + timeout);
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Place this block outside and give it a redstone");
            list.add(TextFormatting.WHITE + "signal around the time that you want to absorb.");
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
