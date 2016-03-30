package mcjty.rftoolsdim.blocks.shards;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DimensionalSmallBlocks extends Block {

    public DimensionalSmallBlocks() {
        super(Material.rock);
        setHardness(2.0f);
        setResistance(4.0f);
        setUnlocalizedName("dimensional_small_blocks");
        setRegistryName("dimensional_small_blocks");
        setLightLevel(0.6f);
        setCreativeTab(RFToolsDim.tabRfToolsDim);
        GameRegistry.registerBlock(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
