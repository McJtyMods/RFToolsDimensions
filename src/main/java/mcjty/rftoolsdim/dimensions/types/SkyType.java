package mcjty.rftoolsdim.dimensions.types;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.util.ResourceLocation;

public enum SkyType {
    SKY_NORMAL,
    SKY_ENDER,
    SKY_INFERNO(new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/plasmasky.png"),
            null, SkyboxType.SKYTYPE_DARKTOP),
    SKY_STARS1(new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars1.png"),
            new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars1a.png"), SkyboxType.SKYTYPE_ALTERNATING),
    SKY_STARS2(new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars2.png"),
            null, SkyboxType.SKYTYPE_ALL),
    SKY_STARS3(new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars3.png"),
            new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars3a.png"), SkyboxType.SKYTYPE_ALLHORIZONTAL);

    public enum SkyboxType {
        SKYTYPE_DARKTOP,
        SKYTYPE_ALLHORIZONTAL,
        SKYTYPE_ALL,
        SKYTYPE_ALTERNATING;
    }

    public final ResourceLocation sky, sky2;
    public final SkyboxType skyboxType;

    private SkyType() {
        this.sky = null;
        this.sky2 = null;
        this.skyboxType = null;
    }

    private SkyType(ResourceLocation sky, ResourceLocation sky2, SkyboxType renderType) {
        this.sky = sky;
        this.sky2 = sky2;
        this.skyboxType = renderType;
    }
}
