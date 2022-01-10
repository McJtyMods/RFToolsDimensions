package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.resources.ResourceLocation;

public class DimensionRegistry {

    public static final ResourceLocation BIOMES_ID = new ResourceLocation(RFToolsDim.MODID, "biomes");

    public static final ResourceLocation RFTOOLS_ID = new ResourceLocation(RFToolsDim.MODID, "rftools");

    public static final ResourceLocation VOID_ID = new ResourceLocation(RFToolsDim.MODID, "void");      // @todo 1.18 deprecated?
    public static final ResourceLocation WAVES_ID = new ResourceLocation(RFToolsDim.MODID, "waves");      // @todo 1.18 deprecated?
    public static final ResourceLocation FLAT_ID = new ResourceLocation(RFToolsDim.MODID, "flat");      // @todo 1.18 deprecated?
    public static final ResourceLocation NORMAL_ID = new ResourceLocation(RFToolsDim.MODID, "normal");      // @todo 1.18 deprecated?
    public static final ResourceLocation ISLANDS_ID = new ResourceLocation(RFToolsDim.MODID, "islands");      // @todo 1.18 deprecated?

    public static final ResourceLocation FIXED_DAY_ID = new ResourceLocation(RFToolsDim.MODID, "fixed_day");
    public static final ResourceLocation FIXED_NIGHT_ID = new ResourceLocation(RFToolsDim.MODID, "fixed_night");
    public static final ResourceLocation NORMAL_TIME_ID = new ResourceLocation(RFToolsDim.MODID, "normal_time");

    public static final ResourceLocation HUT_LOOT = new ResourceLocation(RFToolsDim.MODID, "chests/hut_loot");
}
