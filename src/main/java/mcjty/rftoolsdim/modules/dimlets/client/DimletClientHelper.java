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

}
