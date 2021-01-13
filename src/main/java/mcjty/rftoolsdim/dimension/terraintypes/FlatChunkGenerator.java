package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;

public class FlatChunkGenerator extends BaseChunkGenerator {

    public static final Codec<FlatChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(FlatChunkGenerator::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(FlatChunkGenerator::getSettings)
            ).apply(instance, FlatChunkGenerator::new));

    public FlatChunkGenerator(MinecraftServer server, Settings settings) {
        this(server.func_244267_aX().getRegistry(Registry.BIOME_KEY), settings);
    }

    public FlatChunkGenerator(Registry<Biome> registry, Settings settings) {
        super(registry, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long l) {
        return new FlatChunkGenerator(getBiomeRegistry(), getSettings());
    }

    @Override
    public void generateSurface(WorldGenRegion worldGenRegion, IChunk iChunk) {

    }

    @Override
    public void func_230352_b_(IWorld iWorld, StructureManager structureManager, IChunk chunk) {
        BlockState state = Blocks.STONE.getDefaultState();
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        Heightmap hmOcean = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int y = 0 ; y < 64 ; y++) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    chunk.setBlockState(mpos.setPos(x, y, z), state, false);
                    hmOcean.update(x, y, z, state);
                    hmWorld.update(x, y, z, state);
                }
            }
        }
    }

    @Override
    public int getHeight(int i, int i1, Heightmap.Type type) {
        return 0;   // @todo 1.16
    }

    @Override
    public IBlockReader func_230348_a_(int i, int i1) {
        return new Blockreader(new BlockState[0]);   // @todo 1.16
    }
}
