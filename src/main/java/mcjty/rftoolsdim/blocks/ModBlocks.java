package mcjty.rftoolsdim.blocks;

import mcjty.rftoolsdim.blocks.absorbers.*;
import mcjty.rftoolsdim.blocks.builder.DimensionBuilderBlock;
import mcjty.rftoolsdim.blocks.editor.DimensionEditorBlock;
import mcjty.rftoolsdim.blocks.energyextractor.EnergyExtractorBlock;
import mcjty.rftoolsdim.blocks.enscriber.DimensionEnscriberBlock;
import mcjty.rftoolsdim.blocks.painter.EssencePainterBlock;
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
    public static DimensionEditorBlock dimensionEditorBlock;
    public static DimletWorkbenchBlock dimletWorkbenchBlock;
    public static ActivityProbeBlock activityProbeBlock;
    public static EnergyExtractorBlock energyExtractorBlock;
//    public static EssencePainterBlock essencePainterBlock;

    public static MaterialAbsorberBlock materialAbsorberBlock;
    public static LiquidAbsorberBlock liquidAbsorberBlock;
    public static BiomeAbsorberBlock biomeAbsorberBlock;
    public static TerrainAbsorberBlock terrainAbsorberBlock;
    public static FeatureAbsorberBlock featureAbsorberBlock;
    public static TimeAbsorberBlock timeAbsorberBlock;

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
        dimensionEditorBlock = new DimensionEditorBlock();
        dimletWorkbenchBlock = new DimletWorkbenchBlock();
        activityProbeBlock = new ActivityProbeBlock();
        energyExtractorBlock = new EnergyExtractorBlock();
//        essencePainterBlock = new EssencePainterBlock();

        materialAbsorberBlock = new MaterialAbsorberBlock();
        liquidAbsorberBlock = new LiquidAbsorberBlock();
        biomeAbsorberBlock = new BiomeAbsorberBlock();
        terrainAbsorberBlock = new TerrainAbsorberBlock();
        featureAbsorberBlock = new FeatureAbsorberBlock();
        timeAbsorberBlock = new TimeAbsorberBlock();
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
        dimensionEditorBlock.initModel();
        dimletWorkbenchBlock.initModel();
        activityProbeBlock.initModel();
        energyExtractorBlock.initModel();
//        essencePainterBlock.initModel();

        materialAbsorberBlock.initModel();
        liquidAbsorberBlock.initModel();
        biomeAbsorberBlock.initModel();
        terrainAbsorberBlock.initModel();
        featureAbsorberBlock.initModel();
        timeAbsorberBlock.initModel();
    }
}