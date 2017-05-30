package mcjty.rftoolsdim.dimensions.world.terrain.lost;

import net.minecraft.block.state.IBlockState;

public class Style {
    public IBlockState street;
    public IBlockState street2;
    public IBlockState glass;
    public IBlockState quartz;
    public IBlockState bricks;
    public IBlockState bricks_cracked;
    public IBlockState bricks_mossy;
    public IBlockState bricks_monster;

    public boolean canBeDamagedToIronBars(IBlockState b) {
        return b != null && (b == bricks || b == bricks_cracked || b == bricks_mossy || b == quartz);
    }
}
