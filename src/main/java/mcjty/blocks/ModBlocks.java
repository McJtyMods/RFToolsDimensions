package mcjty.blocks;

import mcjty.blocks.shards.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static DimensionalBlankBlock dimensionalBlankBlock;
    public static DimensionalBlock dimensionalBlock;
    public static DimensionalCross2Block dimensionalCross2Block;
    public static DimensionalCrossBlock dimensionalCrossBlock;
    public static DimensionalPattern1Block dimensionalPattern1Block;
    public static DimensionalPattern2Block dimensionalPattern2Block;
    public static DimensionalSmallBlocks dimensionalSmallBlocks;
    public static DimensionalShardBlock dimensionalShardBlock;

    public static void init() {
        dimensionalBlankBlock = new DimensionalBlankBlock();
        dimensionalBlock = new DimensionalBlock();
        dimensionalCross2Block = new DimensionalCross2Block();
        dimensionalCrossBlock = new DimensionalCrossBlock();
        dimensionalPattern1Block = new DimensionalPattern1Block();
        dimensionalPattern2Block = new DimensionalPattern2Block();
        dimensionalSmallBlocks = new DimensionalSmallBlocks();
        dimensionalShardBlock = new DimensionalShardBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {

    }
}