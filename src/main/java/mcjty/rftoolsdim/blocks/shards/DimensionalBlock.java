package mcjty.rftoolsdim.blocks.shards;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class DimensionalBlock extends Block {

    public DimensionalBlock() {
        super(Material.rock);
        setHardness(2.0f);
        setResistance(4.0f);
        setUnlocalizedName("dimensional_block");
        setRegistryName("dimensional_block");
        setCreativeTab(RFToolsDim.tabRfToolsDim);
    }

    @Override
    public int getLightValue() {
        return 6;
    }
}
