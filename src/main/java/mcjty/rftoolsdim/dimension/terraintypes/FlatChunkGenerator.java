package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.tools.OffsetBlockReader;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureManager;

import javax.annotation.Nonnull;
import java.util.Set;

public class FlatChunkGenerator extends BaseChunkGenerator {

    public static final Codec<FlatChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(FlatChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(FlatChunkGenerator::getDimensionSettings)
            ).apply(instance, FlatChunkGenerator::new));

    public FlatChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        this(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), settings);
    }

    public static final int FLAT_LEVEL = 64;

    public FlatChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
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
        return new FlatChunkGenerator(getBiomeRegistry(), getDimensionSettings());
    }

    @Override
    public void fillFromNoise(@Nonnull IWorld iWorld, @Nonnull StructureManager structureManager, IChunk chunk) {
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        Heightmap hmOcean = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

        Set<AttributeType> attributeTypes = settings.getCompiledDescriptor().getAttributeTypes();
        boolean flatter = attributeTypes.contains(AttributeType.FLATTER);
        boolean elevated = attributeTypes.contains(AttributeType.ELEVATED);

        int flatLevel = FLAT_LEVEL;
        if (flatter) {
            flatLevel /= 2;
        } else if (elevated) {
            flatLevel *= 2;
        }

        for (int y = 0 ; y <= flatLevel ; y++) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    BlockState state = getDefaultBlock();
                    chunk.setBlockState(mpos.set(x, y, z), state, false);
                    hmOcean.update(x, y, z, state);
                    hmWorld.update(x, y, z, state);
                }
            }
        }
    }

    @Override
    public int getBaseHeight(int x, int z, @Nonnull Heightmap.Type type) {
        for (int i = FLAT_LEVEL; i >= 0; --i) {
            BlockState blockstate = defaultBlocks.get(0);
            if (type.isOpaque().test(blockstate)) {
                return i + 1;
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        return new OffsetBlockReader(defaultBlocks.get(0), FLAT_LEVEL);
    }
}
