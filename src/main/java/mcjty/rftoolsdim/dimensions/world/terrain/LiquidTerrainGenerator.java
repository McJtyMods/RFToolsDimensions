package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;

public class LiquidTerrainGenerator implements BaseTerrainGenerator {
    protected GenericChunkProvider provider;

    @Override
    public void setup(World world, GenericChunkProvider provider) {
        this.provider = provider;
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        Block baseLiquid = provider.dimensionInformation.getFluidForTerrain();

        byte waterLevel = 127;

        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int height = 0;
                while (height < 1) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.bedrock.getDefaultState());
                    height++;
                }
                while (height < waterLevel) {
                    BaseTerrainGenerator.setBlockState(primer, index++, baseLiquid.getDefaultState());
                    height++;
                }
                while (height < 256) {
                    BaseTerrainGenerator.setBlockState(primer, index++, Blocks.air.getDefaultState());
                    height++;
                }
            }
        }

    }

    @Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, BiomeGenBase[] biomeGenBases) {
    }

}
