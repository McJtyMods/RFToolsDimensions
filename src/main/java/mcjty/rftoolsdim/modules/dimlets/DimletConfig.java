package mcjty.rftoolsdim.modules.dimlets;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class DimletConfig {

    public static final String SUB_CATEGORY_DIMLETS = "dimlets";

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DIMLET_PACKAGES;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimlets settings").push(SUB_CATEGORY_DIMLETS);

        List<String> defValues = new ArrayList<>();
        defValues.add("base.json");
        defValues.add("vanilla_blocks.json");
        defValues.add("vanilla_fluids.json");
        defValues.add("vanilla_biomes.json");
        defValues.add("rftools.json");
        defValues.add("appliedenergistics2.json");
        defValues.add("biggerreactors.json");
        defValues.add("bigreactors.json");
        defValues.add("botania.json");
        defValues.add("immersiveengineering.json");
        defValues.add("mekanism.json");
        defValues.add("powah.json");
        defValues.add("quark.json");
        defValues.add("tconstruct.json");
        defValues.add("thermal.json");
        defValues.add("biomesoplenty.json");

        DIMLET_PACKAGES = SERVER_BUILDER
                .comment("This is a list of dimlet packages that will be used. Later dimlet packages can override dimlets defined in earlier packages. You can place these packages in the 'config/rftoolsdim' folder")
                .defineList("dimletPackages", defValues, o -> o instanceof String);

        SERVER_BUILDER.pop();
    }

}
