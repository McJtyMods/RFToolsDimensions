package mcjty.blocks.shards;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class DimensionalBlankBlock extends Block {

    public DimensionalBlankBlock() {
        super(Material.rock);
        setHardness(2.0f);
        setResistance(4.0f);
        setUnlocalizedName("dimensional_blank_block");
        setRegistryName("dimensional_blank_block");
        setCreativeTab(RFToolsDim.tabRfToolsDim);
    }

    @Override
    public int getLightValue() {
        return 6;
    }
}
