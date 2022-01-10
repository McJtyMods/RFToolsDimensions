package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

import javax.annotation.Nonnull;

public class IslandsChunkGenerator extends NormalChunkGenerator {

    public static final Codec<IslandsChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(IslandsChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(IslandsChunkGenerator::getDimensionSettings)
            ).apply(instance, IslandsChunkGenerator::new));


    private final BlockState air;

    // @todo 1.18
//    public IslandsChunkGenerator(MinecraftServer server, DimensionSettings settings) {
//        super(server, settings, SETTING_DEFAULT_ISLANDS);
//        air = Blocks.AIR.defaultBlockState();
//    }
//
    public IslandsChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings);
        air = Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void makeBedrock(ChunkAccess chunkIn) {
    }

    @Override
    protected BlockState getBaseLiquid() {
        return air;
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }


}
