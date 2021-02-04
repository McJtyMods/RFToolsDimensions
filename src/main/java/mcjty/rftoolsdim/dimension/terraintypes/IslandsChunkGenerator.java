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

public class IslandsChunkGenerator extends NormalChunkGenerator {

    public static final Codec<IslandsChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(IslandsChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(IslandsChunkGenerator::getSettings)
            ).apply(instance, IslandsChunkGenerator::new));


    private final BlockState air;

    public IslandsChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        super(server, settings, SETTING_DEFAULT_ISLANDS);
        air = Blocks.AIR.getDefaultState();
    }

    public IslandsChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(registry, settings, SETTING_DEFAULT_ISLANDS);
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
