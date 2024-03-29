package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
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
    public void buildSurfaceAndBedrock(@Nonnull WorldGenRegion worldGenRegion, @Nonnull IChunk iChunk) {
        // No surface
    }

    @Override
    protected void makeBedrock(IChunk chunk) {
        // No bedrock
    }

    @Override
    public void fillFromNoise(@Nonnull IWorld iWorld, @Nonnull StructureManager structureManager, @Nonnull IChunk iChunk) {

    }

    @Override
    public int getBaseHeight(int x, int z, @Nonnull Heightmap.Type type) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        return new Blockreader(new BlockState[0]);
    }
}
