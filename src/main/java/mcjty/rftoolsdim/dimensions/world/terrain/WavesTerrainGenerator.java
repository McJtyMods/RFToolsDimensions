package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.config.WorldgenConfiguration;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class WavesTerrainGenerator extends NormalTerrainGenerator {
    private final boolean filled;

    public WavesTerrainGenerator(boolean filled) {
        super();
        this.filled = filled;
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState baseBlock = provider.dimensionInformation.getBaseBlockForTerrain();
        int baseMeta = baseBlock.getBlock().getMetaFromState(baseBlock);           // @todo: need other way to communicate that color array is needed
        Block baseFluid = provider.dimensionInformation.getFluidForTerrain();

        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                double sin = Math.sin((chunkX * 16 + x) / 16.0f);
                double cos = Math.cos((chunkZ * 16 + z) / 16.0f);
                byte waterLevel = (byte) (63 + sin * cos * 16);
                int height = 0;
                while (height < WorldgenConfiguration.bedrockLayer) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.bedrock.getDefaultState());
                    height++;
                }
                if (baseMeta == 127) {
                    while (height < waterLevel) {
                        BaseTerrainGenerator.setBlockState(primer, index++, baseBlock);
                        // @todo this can't work this way! We need the 127 meta information here another way
//                        aBlock[index] = baseBlock;
//                        abyte[index++] = (byte) ((height/2 + x/2 + z/2) & 0xf);
                        height++;
                    }
                } else {
                    while (height < waterLevel) {
                        BaseTerrainGenerator.setBlockState(primer, index++, baseBlock);
                        height++;
                    }
                }
                if (filled) {
                    while (height < 63) {
                        BaseTerrainGenerator.setBlockState(primer, index++, baseFluid.getDefaultState()); // @todo support meta for fluid?
                        height++;
                    }
                }
                while (height < 256) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.air.getDefaultState());
                    height++;
                }
            }
        }

    }
}
