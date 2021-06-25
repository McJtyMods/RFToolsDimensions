package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;

public class IslandsChunkGenerator extends NormalChunkGenerator {

    public static final Codec<IslandsChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(IslandsChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(IslandsChunkGenerator::getDimensionSettings)
            ).apply(instance, IslandsChunkGenerator::new));


    private final BlockState air;

    public IslandsChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        super(server, settings, SETTING_DEFAULT_ISLANDS);
        air = Blocks.AIR.defaultBlockState();
    }

    public IslandsChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings, SETTING_DEFAULT_ISLANDS);
        air = Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void makeBedrock(IChunk chunkIn) {
    }

    @Override
    protected BlockState getBaseLiquid() {
        return air;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }


}
