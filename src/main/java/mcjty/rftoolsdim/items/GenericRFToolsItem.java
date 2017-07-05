package mcjty.rftoolsdim.items;

import mcjty.lib.McJtyRegister;
import mcjty.lib.compat.CompatItem;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GenericRFToolsItem extends CompatItem {

    public GenericRFToolsItem(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(RFToolsDim.tabRfToolsDim);
        McJtyRegister.registerLater(this, RFToolsDim.instance);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


}
