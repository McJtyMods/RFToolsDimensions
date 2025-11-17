package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.resources.ResourceLocation;

public class DimensionRegistry {

    public static final ResourceLocation RFTOOLS_BIOMES_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "biomes");
    public static final ResourceLocation RFTOOLS_CHUNKGEN_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "rftools");
    public static final ResourceLocation RFTOOLS_EFFECTS_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "effects");

    public static final ResourceLocation FIXED_DAY_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "fixed_day");
    public static final ResourceLocation FIXED_NIGHT_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "fixed_night");
    public static final ResourceLocation NORMAL_TIME_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "normal_time");
    public static final ResourceLocation CAVERN_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "cavern");

    public static final ResourceLocation HUT_LOOT = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "chests/hut_loot");
}
