package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.config.WorldgenConfiguration;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class RoughTerrainGenerator extends NormalTerrainGenerator {
    private final boolean filled;

    public RoughTerrainGenerator(boolean filled) {
        super();
        this.filled = filled;
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState baseBlock = provider.dimensionInformation.getBaseBlockForTerrain();
        Block baseFluid = provider.dimensionInformation.getFluidForTerrain();

        Random random = new Random(chunkX * 13L + chunkZ * 577L);

        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                byte waterLevel = (byte) (63 + random.nextFloat() * 32 - 16);
                int height = 0;
                while (height < WorldgenConfiguration.bedrockLayer) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.BEDROCK.getDefaultState());
                    height++;
                }
                // @todo how to support 127
//                if (baseMeta == 127) {
//                    while (height < waterLevel) {
//                        aBlock[index] = baseBlock;
//                        abyte[index++] = (byte) ((height/2 + x/2 + z/2) & 0xf);
//                        height++;
//                    }
//                } else {
                    while (height < waterLevel) {
                        BaseTerrainGenerator.setBlockState(primer, index++, baseBlock);
                        height++;
                    }
//                }
                if (filled) {
                    while (height < 63) {
                        BaseTerrainGenerator.setBlockState(primer, index++, baseFluid.getDefaultState());
                        height++;
                    }
                }
                while (height < 256) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.AIR.getDefaultState());
                    height++;
                }
            }
        }

    }
}
