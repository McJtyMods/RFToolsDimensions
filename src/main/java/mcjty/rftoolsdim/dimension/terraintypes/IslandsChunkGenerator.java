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
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;

public class IslandsChunkGenerator extends NormalChunkGenerator {

    public static final Codec<IslandsChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(IslandsChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(IslandsChunkGenerator::getSettings)
            ).apply(instance, IslandsChunkGenerator::new));


    // From DimensionSettings
    private static final NoiseSettings islands = new NoiseSettings(128,
            new ScalingSettings(2.0D, 1.0D, 80.0D, 160.0D),
            new SlideSettings(-3000, 64, -46), new SlideSettings(-30, 7, 1),
            2, 1, 0.0D, 0.0D, true, false, false, false);

    private final BlockState air;

    @Override
    protected NoiseSettings getNoisesettings() {
        return islands;
    }

    public IslandsChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        super(server, settings, islands);
        air = Blocks.AIR.getDefaultState();
    }

    public IslandsChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings, islands);
        air = Blocks.AIR.getDefaultState();
    }

    @Override
    protected BlockState getBaseLiquid() {
        return air;
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }


}
