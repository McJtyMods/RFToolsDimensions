package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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

public class WavesChunkGenerator extends BaseChunkGenerator {

    private static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("base").forGetter(Settings::getBaseHeight),
                    Codec.FLOAT.fieldOf("verticalvariance").forGetter(Settings::getVerticalVariance),
                    Codec.FLOAT.fieldOf("horizontalvariance").forGetter(Settings::getHorizontalVariance)
            ).apply(instance, Settings::new));

    public static final Codec<WavesChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(WavesChunkGenerator::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(WavesChunkGenerator::getSettings)
            ).apply(instance, WavesChunkGenerator::new));

    public WavesChunkGenerator(MinecraftServer server) {
        this(server.func_244267_aX().getRegistry(Registry.BIOME_KEY), new Settings(65, 30.0f, 30.0f)); // @todo settings?
    }

    public WavesChunkGenerator(Registry<Biome> registry, Settings settings) {
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
    public void generateSurface(WorldGenRegion worldGenRegion, IChunk iChunk) {

    }

    @Override
    public void func_230352_b_(IWorld iWorld, StructureManager structureManager, IChunk chunk) {
        ChunkPos chunkpos = chunk.getPos();

        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = (int) (65 + Math.sin(realx / 20.0f)*10 + Math.cos(realz / 20.0f)*10);
                for (int y = 1 ; y < height ; y++) {
                    chunk.setBlockState(pos.setPos(x, y, z), Blocks.STONE.getDefaultState(), false);
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
