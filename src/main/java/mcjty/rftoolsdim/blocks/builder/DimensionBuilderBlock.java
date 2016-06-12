package mcjty.rftoolsdim.blocks.builder;

import mcjty.lib.api.Infusable;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.theoneprobe.TheOneProbeSupport;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimensionBuilderBlock extends GenericRFToolsBlock<DimensionBuilderTileEntity, DimensionBuilderContainer> implements Infusable {

    public enum OperationType implements IStringSerializable {
        CHARGING("charging"),
        EMPTY("empty"),
        BUILDING1("building1"),
        BUILDING2("building2");

        private final String name;

        OperationType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static final PropertyEnum<OperationType> OPERATIONTYPE = PropertyEnum.create("operationtype", OperationType.class);

    @Override
    public boolean needsRedstoneCheck() {
        return true;
    }

    public DimensionBuilderBlock(boolean creative) {
        super(Material.IRON, DimensionBuilderTileEntity.class, DimensionBuilderContainer.class, "dimension_builder", true);
        setCreative(creative);
    }

    @Override
    public int getGuiID() {
        return RFToolsDim.GUI_DIMENSION_BUILDER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiDimensionBuilder.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This builds a dimension and powers it when");
            list.add(TextFormatting.WHITE + "the dimension is ready.");
            list.add(TextFormatting.YELLOW + "Infusing bonus: reduced power consumption and");
            list.add(TextFormatting.YELLOW + "faster dimension creation speed.");
        } else {
            list.add(TextFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        BlockPos pos = data.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof DimensionBuilderTileEntity) {
            DimensionBuilderTileEntity tileEntity = (DimensionBuilderTileEntity) te;
            NBTTagCompound tagCompound = tileEntity.hasTab();
            if (tagCompound != null) {
                int ticksLeft = tagCompound.getInteger("ticksLeft");
                int tickCost = tagCompound.getInteger("tickCost");
                int pct = (tickCost - ticksLeft) * 100 / tickCost;
                TheOneProbeSupport.addDimensionElement(probeInfo.horizontal(), pct).text(pct + "%");
            }
        }
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        DimensionBuilderTileEntity te = (DimensionBuilderTileEntity) world.getTileEntity(pos);
        return state.withProperty(OPERATIONTYPE, te.getState());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, OPERATIONTYPE);
    }
}
