package mcjty.rftoolsdim.modules.dimlets.client;

import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class DimletClientHelper {

    public static long dimletListAge = 0;
    public static Set<DimletKey> dimlets = new HashSet<>();

    public static void setDimletsOnGui(Set<DimletKey> dimlets) {
        DimletClientHelper.dimlets = dimlets;
        dimletListAge++;
    }

    public static ITextComponent getReadable(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BIOME_CONTROLLER:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BIOME:
                ResourceLocation id = new ResourceLocation(dimletKey.getKey());
                String trans = "biome." + id.getNamespace() + "." + id.getPath();
                return new TranslationTextComponent(trans);
            case FEATURE:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BLOCK:
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dimletKey.getKey()));
                return new TranslationTextComponent(block.getTranslationKey());
        }
        return new StringTextComponent("<unknown>");
    }

    public static String getReadableName(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return dimletKey.getKey().toLowerCase();
            case BIOME_CONTROLLER:
                return dimletKey.getKey().toLowerCase();
            case BIOME:
                ResourceLocation id = new ResourceLocation(dimletKey.getKey());
                String trans = "biome." + id.getNamespace() + "." + id.getPath();
                return I18n.format(trans);
            case FEATURE:
                return dimletKey.getKey().toLowerCase();
            case BLOCK:
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dimletKey.getKey()));
                return I18n.format(block.getTranslationKey());
        }
        return "<unknown>";
    }

}
