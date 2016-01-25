package mcjty.rftoolsdim.blocks;

import mcjty.rftoolsdim.blocks.absorbers.MaterialAbsorberBlock;
import mcjty.rftoolsdim.blocks.builder.DimensionBuilderBlock;
import mcjty.rftoolsdim.blocks.enscriber.DimensionEnscriberBlock;
import mcjty.rftoolsdim.blocks.probe.ActivityProbeBlock;
import mcjty.rftoolsdim.blocks.shards.*;
import mcjty.rftoolsdim.blocks.workbench.DimletWorkbenchBlock;
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

    public static DimensionEnscriberBlock dimensionEnscriberBlock;
    public static DimensionBuilderBlock dimensionBuilderBlock;
    public static DimletWorkbenchBlock dimletWorkbenchBlock;
    public static ActivityProbeBlock activityProbeBlock;

    public static MaterialAbsorberBlock materialAbsorberBlock;

    public static void init() {
        dimensionalBlankBlock = new DimensionalBlankBlock();
        dimensionalBlock = new DimensionalBlock();
        dimensionalCross2Block = new DimensionalCross2Block();
        dimensionalCrossBlock = new DimensionalCrossBlock();
        dimensionalPattern1Block = new DimensionalPattern1Block();
        dimensionalPattern2Block = new DimensionalPattern2Block();
        dimensionalSmallBlocks = new DimensionalSmallBlocks();

        dimensionEnscriberBlock = new DimensionEnscriberBlock();
        dimensionBuilderBlock = new DimensionBuilderBlock(false);
        dimletWorkbenchBlock = new DimletWorkbenchBlock();
        activityProbeBlock = new ActivityProbeBlock();

        materialAbsorberBlock = new MaterialAbsorberBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        dimensionalBlankBlock.initModel();
        dimensionalBlock.initModel();
        dimensionalCross2Block.initModel();
        dimensionalCrossBlock.initModel();
        dimensionalPattern1Block.initModel();
        dimensionalPattern2Block.initModel();
        dimensionalSmallBlocks.initModel();

        dimensionEnscriberBlock.initModel();
        dimensionBuilderBlock.initModel();
        dimletWorkbenchBlock.initModel();
        activityProbeBlock.initModel();

        materialAbsorberBlock.initModel();
    }
}