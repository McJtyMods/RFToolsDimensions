package mcjty.rftoolsdim.items.modules;

import mcjty.rftools.api.screens.IModuleGuiBuilder;
import mcjty.rftools.api.screens.IModuleProvider;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class DimensionModuleItem extends GenericRFToolsItem implements IModuleProvider {

    public DimensionModuleItem() {
        super("dimension_module");
        setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public Class<DimensionScreenModule> getServerScreenModule() {
        return DimensionScreenModule.class;
    }

    @Override
    public Class<DimensionClientScreenModule> getClientScreenModule() {
        return DimensionClientScreenModule.class;
    }

    @Override
    public String getName() {
        return "Dim";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:").text("text", "Label text").color("color", "Color for the label").nl()
                .label("RF+:").color("rfcolor", "Color for the RF text").label("RF-:").color("rfcolor_neg", "Color for the negative", "RF/tick ratio").nl()
                .toggleNegative("hidebar", "Bar", "Toggle visibility of the", "energy bar").mode("RF").format("format").nl()
                .label("Dimension:").integer("dim", "The id of the dimension", "to monitor").nl()
                .choices("align", "Label alignment", "Left", "Center", "Right").nl();

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add(TextFormatting.GREEN + "Uses " + GeneralConfiguration.DIMENSIONMODULE_RFPERTICK + " RF/tick");
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            list.add(TextFormatting.YELLOW + "Label: " + tagCompound.getString("text"));
            list.add(TextFormatting.YELLOW + "Dimension: " + tagCompound.getInteger("dim"));
        }
    }

}