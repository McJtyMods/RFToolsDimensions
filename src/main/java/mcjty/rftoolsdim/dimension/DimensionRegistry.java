package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.resources.ResourceLocation;

public class DimensionRegistry {

    public static final ResourceLocation BIOMES_ID = new ResourceLocation(RFToolsDim.MODID, "biomes");

    public static final ResourceLocation RFTOOLS_ID = new ResourceLocation(RFToolsDim.MODID, "rftools");

    public static final ResourceLocation FIXED_DAY_ID = new ResourceLocation(RFToolsDim.MODID, "fixed_day");
    public static final ResourceLocation FIXED_NIGHT_ID = new ResourceLocation(RFToolsDim.MODID, "fixed_night");
    public static final ResourceLocation NORMAL_TIME_ID = new ResourceLocation(RFToolsDim.MODID, "normal_time");

    public static final ResourceLocation HUT_LOOT = new ResourceLocation(RFToolsDim.MODID, "chests/hut_loot");
}
