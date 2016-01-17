package mcjty.rftoolsdim.blocks.shards;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class DimensionalSmallBlocks extends Block {

    public DimensionalSmallBlocks() {
        super(Material.rock);
        setHardness(2.0f);
        setResistance(4.0f);
        setUnlocalizedName("dimensional_small_blocks");
        setRegistryName("dimensional_small_blocks");
        setCreativeTab(RFToolsDim.tabRfToolsDim);
    }

    @Override
    public int getLightValue() {
        return 6;
    }
}
