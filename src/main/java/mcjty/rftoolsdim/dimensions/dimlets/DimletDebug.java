package mcjty.rftoolsdim.dimensions.dimlets;

import com.google.common.collect.ImmutableMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.DimletRules;
import mcjty.rftoolsdim.config.Filter;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.*;

public class DimletDebug {

    public static void dumpLiquids() {
        FluidRegistry.getRegisteredFluids().entrySet().stream().forEach(DimletDebug::dumpLiquid);
    }

    private static void dumpLiquid(Map.Entry<String, Fluid> entry) {
        Block block = entry.getValue().getBlock();
        if (block != null) {
            ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(block);
            String mod = nameForObject.getResourceDomain();
            DimletKey key = new DimletKey(DimletType.DIMLET_LIQUID, block.getRegistryName() + "@0");
            Settings settings = DimletRules.getSettings(key, mod);
            Logging.log(key + ": " + settings.toString());
        }
    }

    public static void dumpBlocks() {
        Block.blockRegistry.forEach(DimletDebug::dumpBlock);
    }

    private static void dumpBlock(Block block) {
        if (block instanceof BlockLiquid) {
            return;
        }

        Set<Filter.Feature> features = KnownDimletConfiguration.getBlockFeatures(block);
        String mod = Block.blockRegistry.getNameForObject(block).getResourceDomain();

        for (IBlockState state : block.getBlockState().getValidStates()) {
            int meta = state.getBlock().getMetaFromState(state);
            List<IProperty> propertyNames = new ArrayList<>(state.getPropertyNames());
            propertyNames.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

            ImmutableMap<IProperty, Comparable> properties = state.getProperties();
            Map<String, String> props = new HashMap<>();
            for (Map.Entry<IProperty, Comparable> entry : properties.entrySet()) {
                props.put(entry.getKey().getName(), entry.getValue().toString());
            }
            DimletKey key = new DimletKey(DimletType.DIMLET_MATERIAL, block.getRegistryName() + "@" + meta);
            Settings settings = DimletRules.getSettings(key, mod, features, props);
            Logging.log(key + " (" + state.toString() + "): " + settings.toString());
        }

    }

    public static void dumpDimlets() {
        Map<DimletKey, Settings> knownDimlets = KnownDimletConfiguration.getKnownDimlets();
        List<DimletKey> keys = new ArrayList<>(knownDimlets.keySet());
        keys.sort((o1, o2) -> {
            int i = o1.getType().compareTo(o2.getType());
            if (i != 0) {
                return i;
            }
            if (o1.getId() != null && o2.getId() != null) {
                return o1.getId().compareTo(o2.getId());
            }
            if (o1.getId() == null && o2.getId() == null) {
                return 0;
            }
            if (o1.getId() == null) {
                return -1;
            }
            return 1;
        });
        for (DimletKey key : keys) {
            Settings value = knownDimlets.get(key);
            Logging.log(key + ": " + value);
        }

    }
}
