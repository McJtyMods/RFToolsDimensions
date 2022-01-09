package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseChunkGenerator extends NoiseBasedChunkGenerator {

    protected final DimensionSettings settings;

    protected final List<BlockState> defaultBlocks = new ArrayList<>();
    private final Random random;
    private BlockState defaultFluid;

    // @todo 1.18!!
    public BaseChunkGenerator(RegistryAccess registryAccess, DimensionSettings settings) {
        super(registryAccess.registryOrThrow(Registry.NOISE_REGISTRY),
                new RFTBiomeProvider(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), settings),
                settings.getSeed(), () -> registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).get(NoiseGeneratorSettings.OVERWORLD));
        this.settings = settings;
        random = new Random(settings.getSeed());
//        this.surfaceDepthNoise = (INoiseGenerator)(noisesettings.useSimplexSurfaceNoise() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
        defaultBlocks.addAll(settings.getCompiledDescriptor().getBaseBlocks());
        defaultFluid = settings.getCompiledDescriptor().getBaseLiquid();
        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.NOOCEANS)) {
            defaultFluid = Blocks.AIR.defaultBlockState();
        }
    }

    // Get a (possibly random) default block
    public BlockState getDefaultBlock() {
        if (defaultBlocks.size() == 1) {
            return defaultBlocks.get(0);
        } else {
            int idx = random.nextInt(defaultBlocks.size());
            return defaultBlocks.get(idx);
        }
    }

    public DimensionSettings getDimensionSettings() {
        return settings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((RFTBiomeProvider)biomeSource).getBiomeRegistry();
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
        super.buildSurface(region, structureFeatureManager, chunk);
    }

    protected BlockState getBaseLiquid() {
        return this.defaultFluid;
    }


    protected void makeBedrock(ChunkAccess chunkIn) {
        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.NOBEDROCK)) {
            return;
        }
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        int xs = chunkIn.getPos().getMinBlockX();
        int zs = chunkIn.getPos().getMinBlockZ();
        for(BlockPos blockpos : BlockPos.betweenClosed(xs, 0, zs, xs + 15, 0, zs + 15)) {
            for(int y = 4; y >= 0; --y) {
                if (y <= random.nextInt(5)) {
                    chunkIn.setBlockState(mpos.set(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                }
            }
        }
    }

}
