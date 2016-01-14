package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.dimensions.DimletConfiguration;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class FlatTerrainGenerator extends NormalTerrainGenerator {
    private final byte height;

    public FlatTerrainGenerator(byte height) {
        super();
        this.height = height;
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState baseBlock = provider.dimensionInformation.getBaseBlockForTerrain();

        byte waterLevel = height;

        boolean elevated = false;
        if (provider.dimensionInformation.hasFeatureType(FeatureType.FEATURE_MAZE)) {
            long s2 = ((chunkX + provider.seed + 13) * 314) + chunkZ * 17L;
            Random rand = new Random(s2);
            rand.nextFloat();   // Skip one.
            elevated = (chunkX & 1) == 0;
            if (rand.nextFloat() < .2f) {
                elevated = !elevated;
            }
            if (elevated) {
                waterLevel = 120;
            } else {
                waterLevel = 40;
            }
        }

        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int height = 0;
                while (height < DimletConfiguration.bedrockLayer) {
                    primer.setBlockState(index++, Blocks.bedrock.getDefaultState());
                    height++;
                }
                // @todo support 127
//                if (baseMeta == 127) {
//                    while (height < waterLevel) {
//                        aBlock[index] = baseBlock;
//                        abyte[index++] = (byte) ((height/2 + x/2 + z/2) & 0xf);
//                        height++;
//                    }
//                } else {
                    while (height < waterLevel) {
                        primer.setBlockState(index++, baseBlock);
                        height++;
                    }
//                }
                while (height < 256) {
                    primer.setBlockState(index++, Blocks.air.getDefaultState());
                    height++;
                }
            }
        }

    }
}
