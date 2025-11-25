package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class DimensionRegistry {

    public static final ResourceLocation RFTOOLS_EFFECTS_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "effects");

    public static final ResourceLocation FIXED_DAY_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "fixed_day");
    public static final ResourceLocation FIXED_NIGHT_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "fixed_night");
    public static final ResourceLocation NORMAL_TIME_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "normal_time");
    public static final ResourceLocation CAVERN_ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "cavern");

    public static final ResourceKey<LootTable> HUT_LOOT = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "hut_loot"));
}
