package mcjty.rftoolsdim.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class RFToolsDimensionsTOPDriver implements TOPDriver {

    public static final RFToolsDimensionsTOPDriver DRIVER = new RFToolsDimensionsTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = blockState.getBlock().getRegistryName();
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() == EssencesModule.BLOCK_ABSORBER.get()) {
                drivers.put(id, new BlockAbsorberDriver());
            } else if (blockState.getBlock() == EssencesModule.FLUID_ABSORBER.get()) {
                drivers.put(id, new FluidAbsorberDriver());
            } else if (blockState.getBlock() == EssencesModule.BIOME_ABSORBER.get()) {
                drivers.put(id, new BiomeAbsorberDriver());
            } else {
                drivers.put(id, new DefaultDriver());
            }
        }
        TOPDriver driver = drivers.get(id);
        if (driver != null) {
            driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class DefaultDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class BlockAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (BlockAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                Block block = te.getAbsorbingBlock();
                int pct = ((EssencesConfig.maxBlockAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBlockAbsorption.get();
                ItemStack stack = new ItemStack(block, 1);
                probeInfo.text((new StringTextComponent("Block: ").appendSibling(new TranslationTextComponent(stack.getTranslationKey())).mergeStyle(TextFormatting.GREEN)))
                        .horizontal()
                        .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"))
                        .item(stack);
            }, "Bad tile entity!");
        }
    }

    private static class FluidAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (FluidAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                Block block = te.getAbsorbingBlock();
                if (block != null) {
                    int pct = ((EssencesConfig.maxFluidAbsorption.get() - absorbing) * 100) / EssencesConfig.maxFluidAbsorption.get();
                    probeInfo.text((new StringTextComponent("Fluid: ").appendSibling(new TranslationTextComponent(block.getTranslationKey())).mergeStyle(TextFormatting.GREEN)))
                            .horizontal()
                            .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
                }
            }, "Bad tile entity!");
        }
    }

    private static class BiomeAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (BiomeAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                String biome = te.getAbsorbingBiome();
                int pct = ((EssencesConfig.maxBiomeAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBiomeAbsorption.get();
                ResourceLocation id = new ResourceLocation(biome);
                String trans = "biome." + id.getNamespace() + "." + id.getPath();

                probeInfo.text((new StringTextComponent("Biome: ").appendSibling(new TranslationTextComponent(trans)).mergeStyle(TextFormatting.GREEN)))
                        .horizontal()
                        .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
            }, "Bad tile entity!");
        }
    }

}
