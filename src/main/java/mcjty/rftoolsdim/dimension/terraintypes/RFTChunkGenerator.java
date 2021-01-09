package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
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

public class RFTChunkGenerator extends BaseChunkGenerator {

    private static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("base").forGetter(Settings::getBaseHeight),
                    Codec.FLOAT.fieldOf("verticalvariance").forGetter(Settings::getVerticalVariance),
                    Codec.FLOAT.fieldOf("horizontalvariance").forGetter(Settings::getHorizontalVariance)
            ).apply(instance, Settings::new));

    public static final Codec<RFTChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(RFTChunkGenerator::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(RFTChunkGenerator::getSettings)
            ).apply(instance, RFTChunkGenerator::new));

    public RFTChunkGenerator(Registry<Biome> registry, Settings settings) {
        super(registry, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long l) {
        return new RFTChunkGenerator(getBiomeRegistry(), getSettings());
    }

    @Override
    public void generateSurface(WorldGenRegion worldGenRegion, IChunk iChunk) {

    }

    @Override
    public void func_230352_b_(IWorld iWorld, StructureManager structureManager, IChunk iChunk) {

    }

    @Override
    public int getHeight(int i, int i1, Heightmap.Type type) {
        return 0;       // @todo 1.16
    }

    @Override
    public IBlockReader func_230348_a_(int i, int i1) {
        return new Blockreader(new BlockState[0]);   // @todo 1.16
    }

}
