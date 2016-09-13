package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.dimensions.description.SkyDescriptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkyRegistry {

    private static final Map<DimletKey, SkyDescriptor> skyDescriptorMap = new HashMap<>();
    private static final Set<DimletKey> skyBodies = new HashSet<>();

    public static void registerSky(DimletKey key, SkyDescriptor descriptor, boolean body) {
        skyDescriptorMap.put(key, descriptor);
        if (body) {
            skyBodies.add(key);
        }
    }

    public static SkyDescriptor getSkyDescriptor(DimletKey key) {
        return skyDescriptorMap.get(key);
    }

    public static boolean isSkyBody(DimletKey key) {
        return skyBodies.contains(key);
    }
}
