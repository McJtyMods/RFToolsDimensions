package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.DimensionSettings;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
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

public class VoidChunkGenerator extends BaseChunkGenerator {

    public static final Codec<VoidChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(VoidChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(VoidChunkGenerator::getSettings)
            ).apply(instance, VoidChunkGenerator::new));

    public VoidChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        this(server.func_244267_aX().getRegistry(Registry.BIOME_KEY), settings);
    }

    public VoidChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long l) {
        return new VoidChunkGenerator(getBiomeRegistry(), getSettings());
    }

    @Override
    public void generateSurface(WorldGenRegion worldGenRegion, IChunk iChunk) {
        // No surface
    }

    @Override
    protected void makeBedrock(IChunk chunk) {
        // No bedrock
    }

    @Override
    public void func_230352_b_(IWorld iWorld, StructureManager structureManager, IChunk iChunk) {

    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type type) {
        return 0;
    }

    @Override
    public IBlockReader func_230348_a_(int x, int z) {
        return new Blockreader(new BlockState[0]);
    }
}
