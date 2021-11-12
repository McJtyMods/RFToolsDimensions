package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.block.AbstractBlock;

public class BiomeAbsorberTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public BiomeAbsorberTileEntity() {
        super(EssencesModule.TYPE_BIOME_ABSORBER.get());
    }

    private int absorbing = 0;
    private String biomeId = null;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(AbstractBlock.Properties.of(Material.METAL)
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
        String biome = NBTTools.getInfoNBT(stack, CompoundNBT::getString, "biome", null);
        if (biome == null) {
            return "<Not Set>";
        } else {
            ResourceLocation id = new ResourceLocation(biome);
            String trans = "biome." + id.getNamespace() + "." + id.getPath();
            return I18n.get(trans);
        }
    }

    public static String getBiome(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundNBT::getString, "biome", null);
    }

    private static String getProgressName(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxBiomeAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBiomeAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return -1;
        } else {
            return ((EssencesConfig.maxBiomeAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBiomeAbsorption.get();
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            tickClient();
        } else {
            tickServer();
        }
    }

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

    protected void tickServer() {
        if (biomeId == null) {
            Biome biome = getLevel().getBiome(getBlockPos());
            biomeId = biome.getRegistryName().toString();
            absorbing = EssencesConfig.maxBiomeAbsorption.get();
            setChanged();
        }

        if (absorbing > 0) {
            Biome biome = level.getBiome(worldPosition);
            if (biome == null || !biome.getRegistryName().toString().equals(biomeId)) {
                return;
            }

            absorbing--;
            setChanged();
        }
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("absorbing", absorbing);
        if (biomeId != null) {
            info.putString("biome", biomeId);
        }
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        if (info != null) {
            absorbing = info.getInt("absorbing");
            if (info.contains("biome")) {
                biomeId = info.getString("biome");
            } else {
                biomeId = null;
            }
        }
    }



}
