package mcjty.rftoolsdim.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.StructureAbsorberTileEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class RFToolsDimensionsTOPDriver implements TOPDriver {

    public static final RFToolsDimensionsTOPDriver DRIVER = new RFToolsDimensionsTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = Tools.getId(blockState);
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() == EssencesModule.BLOCK_ABSORBER.get()) {
                drivers.put(id, new BlockAbsorberDriver());
            } else if (blockState.getBlock() == EssencesModule.FLUID_ABSORBER.get()) {
                drivers.put(id, new FluidAbsorberDriver());
            } else if (blockState.getBlock() == EssencesModule.BIOME_ABSORBER.get()) {
                drivers.put(id, new BiomeAbsorberDriver());
            } else if (blockState.getBlock() == EssencesModule.STRUCTURE_ABSORBER.get()) {
                drivers.put(id, new StructureAbsorberDriver());
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
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class BlockAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (BlockAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                Block block = te.getAbsorbingBlock();
                int pct = ((EssencesConfig.maxBlockAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBlockAbsorption.get();
                ItemStack stack = new ItemStack(block, 1);
                if (!stack.isEmpty()) {
                    probeInfo.text((ComponentFactory.literal("Block: ").append(ComponentFactory.translatable(stack.getDescriptionId())).withStyle(ChatFormatting.GREEN)))
                            .horizontal()
                            .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"))
                            .item(stack);
                }
            }, "Bad tile entity!");
        }
    }

    private static class FluidAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (FluidAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                Block block = te.getAbsorbingBlock();
                if (block != null) {
                    int pct = ((EssencesConfig.maxFluidAbsorption.get() - absorbing) * 100) / EssencesConfig.maxFluidAbsorption.get();
                    probeInfo.text((ComponentFactory.literal("Fluid: ").append(ComponentFactory.translatable(block.getDescriptionId())).withStyle(ChatFormatting.GREEN)))
                            .horizontal()
                            .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
                }
            }, "Bad tile entity!");
        }
    }

    private static class BiomeAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (BiomeAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                String biome = te.getAbsorbingBiome();
                int pct = ((EssencesConfig.maxBiomeAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBiomeAbsorption.get();
                ResourceLocation id = new ResourceLocation(biome);
                String trans = "biome." + id.getNamespace() + "." + id.getPath();

                probeInfo.text((ComponentFactory.literal("Biome: ").append(ComponentFactory.translatable(trans)).withStyle(ChatFormatting.GREEN)))
                        .horizontal()
                        .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
            }, "Bad tile entity!");
        }
    }

    private static class StructureAbsorberDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (StructureAbsorberTileEntity te) -> {
                int absorbing = te.getAbsorbing();
                String structure = te.getAbsorbingStructure();
                if (structure != null) {
                    int pct = ((EssencesConfig.maxStructureAbsorption.get() - absorbing) * 100) / EssencesConfig.maxStructureAbsorption.get();
                    ResourceLocation id = new ResourceLocation(structure);

                    probeInfo.text((ComponentFactory.literal("Structure: ").append(ComponentFactory.literal(id.getPath())).withStyle(ChatFormatting.GREEN)))
                            .horizontal()
                            .progress(pct, 100, probeInfo.defaultProgressStyle().suffix("%"));
                }
            }, "Bad tile entity!");
        }
    }

}
