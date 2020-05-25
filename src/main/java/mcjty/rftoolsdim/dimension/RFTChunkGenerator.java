package mcjty.rftoolsdim.dimension;

import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;

public class RFTChunkGenerator extends ChunkGenerator<RFTChunkGenerator.Config> {

    public RFTChunkGenerator(IWorld world, BiomeProvider biomeProvider) {
        super(world, biomeProvider, Config.createDefault());
    }

    @Override
    public void func_225551_a_(WorldGenRegion region, IChunk chunk) {

    }

    @Override
    public int getGroundHeight() {
        return world.getSeaLevel()+1;
    }

    @Override
    public void makeBase(IWorld world, IChunk chunk) {

    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
        return 0;
    }

    public static class Config extends GenerationSettings {

        public static Config createDefault() {
            Config config = new Config();
            config.setDefaultBlock(Blocks.DIAMOND_BLOCK.getDefaultState());
            config.setDefaultFluid(Blocks.LAVA.getDefaultState());
            return config;
        }

    }

}
