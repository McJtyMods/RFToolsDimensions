package mcjty.rftoolsdim.items.parts;

import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimletBaseItem extends GenericRFToolsItem {
    public DimletBaseItem() {
        super("dimlet_base");
        setMaxStackSize(64);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This is a basic part of a dimlet. You can get this");
            list.add(TextFormatting.WHITE + "by deconstructing other dimlets in the Dimlet");
            list.add(TextFormatting.WHITE + "Workbench. In that same workbench you can also use");
            list.add(TextFormatting.WHITE + "this item to make new dimlets.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }
}
