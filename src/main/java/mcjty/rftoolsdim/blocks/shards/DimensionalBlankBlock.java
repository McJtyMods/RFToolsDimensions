package mcjty.rftoolsdim.blocks.shards;

import mcjty.lib.McJtyRegister;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DimensionalBlankBlock extends Block {

    public DimensionalBlankBlock() {
        super(Material.ROCK);
        setHardness(2.0f);
        setResistance(4.0f);
        setUnlocalizedName(RFToolsDim.MODID + "." + "dimensional_blank_block");
        setRegistryName("dimensional_blank_block");
        setLightLevel(0.6f);
        setCreativeTab(RFToolsDim.setup.getTab());
        McJtyRegister.registerLater(this, RFToolsDim.instance, ItemBlock::new);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
