package mcjty.rftoolsdim.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EmptyDimensionTab extends GenericRFToolsItem {

    public EmptyDimensionTab() {
        super("empty_dimension_tab");
        setMaxStackSize(16);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        list.add(EnumChatFormatting.YELLOW + "Put this empty dimension tab in a 'Dimension Enscriber'");
        list.add(EnumChatFormatting.YELLOW + "where you can construct a dimension using dimlets");
    }
}
