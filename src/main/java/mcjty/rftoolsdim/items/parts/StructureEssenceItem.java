package mcjty.rftoolsdim.items.parts;

import mcjty.rftoolsdim.dimensions.types.StructureType;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class StructureEssenceItem extends GenericRFToolsItem {

    public StructureEssenceItem() {
        super("structure_essence");
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation resource = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomMeshDefinition(this, stack -> resource);
    }

    private String calculateUnlocalizedNameSuffix(StructureType type) {
        return "_" + StringUtils.uncapitalize(type.getId());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This essence item is the main ingredient for");
            list.add(TextFormatting.WHITE + "structure dimlets.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + calculateUnlocalizedNameSuffix(StructureType.values()[itemStack.getItemDamage()]);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            for (StructureType type : StructureType.values()) {
                subItems.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }
}
