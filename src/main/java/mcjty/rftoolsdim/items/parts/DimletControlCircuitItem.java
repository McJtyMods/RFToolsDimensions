package mcjty.rftoolsdim.items.parts;

import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimletControlCircuitItem extends GenericRFToolsItem {

    public DimletControlCircuitItem() {
        super("dimlet_control_circuit");
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation models[] = new ModelResourceLocation[7];
        for (int i = 0 ; i <= 6 ; i++) {
            ResourceLocation registryName = getRegistryName();
            registryName = new ResourceLocation(registryName.getResourceDomain(), registryName.getResourcePath() + i);
            models[i] = new ModelResourceLocation(registryName, "inventory");
            ModelBakery.registerItemVariants(this, models[i]);
        }

        ModelLoader.setCustomMeshDefinition(this, stack -> models[stack.getItemDamage()]);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Every dimlet needs one control circuit. You can get");
            list.add(TextFormatting.WHITE + "this by deconstructing other dimlets in the Dimlet");
            list.add(TextFormatting.WHITE + "Workbench. In that same workbench you can also use");
            list.add(TextFormatting.WHITE + "this item to make new dimlets. Note that you need a");
            list.add(TextFormatting.WHITE + "control circuit of the right rarity in order to make");
            list.add(TextFormatting.WHITE + "a dimlet of that rarity.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            for (int i = 0; i < 7; i++) {
                subItems.add(new ItemStack(this, 1, i));
            }
        }
    }
}
