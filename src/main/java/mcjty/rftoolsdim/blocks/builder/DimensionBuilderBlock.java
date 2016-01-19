package mcjty.rftoolsdim.blocks.builder;

import mcjty.lib.api.Infusable;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimensionBuilderBlock extends GenericRFToolsBlock<DimensionBuilderTileEntity, DimensionBuilderContainer> implements Infusable {

    public static enum OperationType implements IStringSerializable {
        CHARGING("charging"),
        BUILDING1("building1"),
        BUILDING2("building2"),
        EMPTY("empty");

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


    public DimensionBuilderBlock(boolean creative) {
        super(Material.iron, DimensionBuilderTileEntity.class, DimensionBuilderContainer.class, "dimension_builder", true);
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
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This builds a dimension and powers it when");
            list.add(EnumChatFormatting.WHITE + "the dimension is ready.");
            list.add(EnumChatFormatting.YELLOW + "Infusing bonus: reduced power consumption and");
            list.add(EnumChatFormatting.YELLOW + "faster dimension creation speed.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        checkRedstoneWithTE(world, pos);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        DimensionBuilderTileEntity te = (DimensionBuilderTileEntity) world.getTileEntity(pos);
        return state.withProperty(OPERATIONTYPE, te.getState());
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, OPERATIONTYPE);
    }
}
