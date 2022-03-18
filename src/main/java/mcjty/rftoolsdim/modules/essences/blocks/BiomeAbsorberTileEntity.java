package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BiomeAbsorberTileEntity extends TickingTileEntity {

    public BiomeAbsorberTileEntity(BlockPos pos, BlockState state) {
        super(EssencesModule.TYPE_BIOME_ABSORBER.get(), pos, state);
    }

    private int absorbing = 0;
    private String biomeId = null;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of(Material.METAL)
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
        String biome = NBTTools.getInfoNBT(stack, CompoundTag::getString, "biome", null);
        if (biome == null) {
            return "<Not Set>";
        } else {
            ResourceLocation id = new ResourceLocation(biome);
            String trans = "biome." + id.getNamespace() + "." + id.getPath();
            return I18n.get(trans);
        }
    }

    public static String getBiome(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getString, "biome", null);
    }

    private static String getProgressName(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxBiomeAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBiomeAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return -1;
        } else {
            return ((EssencesConfig.maxBiomeAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBiomeAbsorption.get();
        }
    }

    @Override
    protected void tickClient() {
        if (absorbing > 0) {
            Random rand = level.random;

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
        return absorbing;
    }

    public String getAbsorbingBiome() {
        return biomeId;
    }

    @Override
    protected void tickServer() {
        if (biomeId == null) {
            Holder<Biome> biome = getLevel().getBiome(getBlockPos());
            biomeId = biome.value().getRegistryName().toString();
            absorbing = EssencesConfig.maxBiomeAbsorption.get();
            setChanged();
        }

        if (absorbing > 0) {
            Holder<Biome> biome = level.getBiome(worldPosition);
            if (!biome.value().getRegistryName().toString().equals(biomeId)) {
                return;
            }

            absorbing--;
            setChanged();
        }
    }

    @Override
    public void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        info.putInt("absorbing", absorbing);
        if (biomeId != null) {
            info.putString("biome", biomeId);
        }
    }

    @Override
    public void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        absorbing = info.getInt("absorbing");
        if (info.contains("biome")) {
            biomeId = info.getString("biome");
        } else {
            biomeId = null;
        }
    }
}
