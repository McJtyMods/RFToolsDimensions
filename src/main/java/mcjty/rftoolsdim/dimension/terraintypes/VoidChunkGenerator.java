package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;

import javax.annotation.Nonnull;

public class VoidChunkGenerator extends BaseChunkGenerator {

    public static final Codec<VoidChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(VoidChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(VoidChunkGenerator::getDimensionSettings)
            ).apply(instance, VoidChunkGenerator::new));

    public VoidChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        this(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), settings);
    }

    public VoidChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings);
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public ChunkGenerator withSeed(long l) {
        return new VoidChunkGenerator(getBiomeRegistry(), getDimensionSettings());
    }

    @Override
    public void buildSurfaceAndBedrock(@Nonnull WorldGenRegion worldGenRegion, @Nonnull ChunkAccess iChunk) {
        // No surface
    }

    @Override
    protected void makeBedrock(ChunkAccess chunk) {
        // No bedrock
    }

    @Override
    public void fillFromNoise(@Nonnull LevelAccessor iWorld, @Nonnull StructureFeatureManager structureManager, @Nonnull ChunkAccess iChunk) {

    }

    @Override
    public int getBaseHeight(int x, int z, @Nonnull Heightmap.Types type) {
        return 0;
    }

    @Nonnull
    @Override
    public BlockGetter getBaseColumn(int x, int z) {
        return new NoiseColumn(new BlockState[0]);
    }
}
