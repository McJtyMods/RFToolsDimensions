package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.tools.OffsetBlockReader;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureManager;

public class WavesChunkGenerator extends BaseChunkGenerator {

    public static final Codec<WavesChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(WavesChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(WavesChunkGenerator::getSettings)
            ).apply(instance, WavesChunkGenerator::new));

    public WavesChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        this(server.getDynamicRegistries().getRegistry(Registry.BIOME_KEY), settings);
    }

    public WavesChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long l) {
        return new WavesChunkGenerator(getBiomeRegistry(), getSettings());
    }

    @Override
    public void func_230352_b_(IWorld iWorld, StructureManager structureManager, IChunk chunk) {
        ChunkPos chunkpos = chunk.getPos();

        BlockPos.Mutable mpos = new BlockPos.Mutable();

        Heightmap hmOcean = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = calculateWaveHeight(realx, realz);
                for (int y = 1 ; y < height ; y++) {
                    BlockState state = getDefaultBlock();
                    chunk.setBlockState(mpos.setPos(x, y, z), state, false);
                    hmOcean.update(x, y, z, state);
                    hmWorld.update(x, y, z, state);
                }
            }
        }
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type type) {
        int realx = x;  // @todo 1.16 is this the actual x/z?
        int realz = z;
        int height = calculateWaveHeight(realx, realz);
        for (int i = height; i >= 0; --i) {
            BlockState blockstate = defaultBlocks.get(0);
            if (type.getHeightLimitPredicate().test(blockstate)) {
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public IBlockReader func_230348_a_(int x, int z) {
        int realx = x;  // @todo 1.16 is this the actual x/z?
        int realz = z;
        int height = calculateWaveHeight(realx, realz);
        return new OffsetBlockReader(defaultBlocks.get(0), height);
    }

    private int calculateWaveHeight(int realx, int realz) {
        return (int) (65 + Math.sin(realx / 20.0f) * 10 + Math.cos(realz / 20.0f) * 10);
    }

}
