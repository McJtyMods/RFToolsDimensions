package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.data.BiomeAbsorberData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BiomeAbsorberTileEntity extends TickingTileEntity {

    public BiomeAbsorberTileEntity(BlockPos pos, BlockState state) {
        super(EssencesModule.TYPE_BIOME_ABSORBER.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .tileEntitySupplier(BiomeAbsorberTileEntity::new)
                .topDriver(RFToolsDimensionsTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/dimlet_workbench"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(),
                        parameter("block", BiomeAbsorberTileEntity::getBiomeName),
                        parameter("progress", BiomeAbsorberTileEntity::getProgressName)
                )) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    private static String getBiomeName(ItemStack stack) {
        BiomeAbsorberData data = stack.get(EssencesModule.ITEM_BIOME_ABSORBER_DATA);
        if  (data == null || data.biome() == null) {
            return "<Not Set>";
        } else {
            return I18n.get(data.biome().toLanguageKey(Registries.BIOME.location().getPath()).replace('/', '.'));
        }
    }

    public static ResourceLocation getBiome(ItemStack stack) {
        BiomeAbsorberData data = stack.getOrDefault(EssencesModule.ITEM_BIOME_ABSORBER_DATA, BiomeAbsorberData.DEFAULT);
        return data.biome();
    }

    private static String getProgressName(ItemStack stack) {
        BiomeAbsorberData data = stack.get(EssencesModule.ITEM_BIOME_ABSORBER_DATA);
        if  (data == null) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxBiomeAbsorption.get() - data.absorbing()) * 100) / EssencesConfig.maxBiomeAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        BiomeAbsorberData data = stack.get(EssencesModule.ITEM_BIOME_ABSORBER_DATA);
        if (data == null) {
            return -1;
        } else {
            return ((EssencesConfig.maxBiomeAbsorption.get() - data.absorbing()) * 100) / EssencesConfig.maxBiomeAbsorption.get();
        }
    }

    @Override
    protected void tickClient() {
        BiomeAbsorberData data = getData(EssencesModule.BIOME_ABSORBER_DATA);
        if (data.absorbing() > 0) {
            RandomSource rand = level.random;

            double u = rand.nextFloat() * 2.0f - 1.0f;
            double v = (float) (rand.nextFloat() * 2.0f * Math.PI);
            double x = Math.sqrt(1 - u * u) * Math.cos(v);
            double y = Math.sqrt(1 - u * u) * Math.sin(v);
            double z = u;
            double r = 1.0f;

            level.addParticle(ParticleTypes.PORTAL, getBlockPos().getX() + 0.5f + x * r, getBlockPos().getY() + 0.5f + y * r, getBlockPos().getZ() + 0.5f + z * r, -x, -y, -z);
        }
    }

    public int getAbsorbing() {
        BiomeAbsorberData data = getData(EssencesModule.BIOME_ABSORBER_DATA);
        return data.absorbing();
    }

    @Nullable
    public ResourceLocation getAbsorbingBiome() {
        BiomeAbsorberData data = getData(EssencesModule.BIOME_ABSORBER_DATA);
        return data.biome();
    }

    @Override
    protected void tickServer() {
        BiomeAbsorberData data = getData(EssencesModule.BIOME_ABSORBER_DATA);
        ResourceLocation biomeId = data.biome();
        int absorbing = data.absorbing();
        if (biomeId == null) {
            Holder<Biome> biome = getLevel().getBiome(getBlockPos());
            biomeId = Tools.getId(level, biome.value());
            absorbing = EssencesConfig.maxBiomeAbsorption.get();
        }

        if (absorbing > 0) {
            Holder<Biome> biome = level.getBiome(worldPosition);
            if (!Tools.getId(level, biome.value()).equals(biomeId)) {
                return;
            }
            absorbing--;
        }
        setData(EssencesModule.BIOME_ABSORBER_DATA, new BiomeAbsorberData(biomeId, absorbing));
    }
}
