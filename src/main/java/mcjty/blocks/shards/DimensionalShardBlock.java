package mcjty.blocks.shards;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class DimensionalShardBlock extends Block {

    public DimensionalShardBlock() {
        super(Material.rock);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setUnlocalizedName("dimensional_shard_block");
        setRegistryName("dimensional_shard_block");
        setCreativeTab(RFToolsDim.tabRfToolsDim);
    }

    @Override
    public int getLightValue() {
        return 6;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) {
            for (int i = 0 ; i < 10 ; i++) {
                world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, rand.nextGaussian() / 3.0f, rand.nextGaussian() / 3.0f, rand.nextGaussian() / 3.0f);
            }
        }
    }

//    @Override
//    public Item getItemDropped(int p_149650_1_, Random random, int p_149650_3_) {
//        return DimletSetup.dimensionalShard;
//    }
    // @todo

    @Override
    public int quantityDropped(Random random) {
        return 2 + random.nextInt(3);
    }

    @Override
    public int quantityDroppedWithBonus(int bonus, Random random) {
        int j = random.nextInt(bonus + 2) - 1;
        if (j < 0) {
            j = 0;
        }

        return this.quantityDropped(random) * (j + 1);
    }

    private Random rand = new Random();

    @Override
    public int getExpDrop(IBlockAccess world, BlockPos pos, int fortune) {
        return MathHelper.getRandomIntegerInRange(rand, 3, 7);
    }
}
