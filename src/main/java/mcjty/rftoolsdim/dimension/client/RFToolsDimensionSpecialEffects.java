package mcjty.rftoolsdim.dimension.client;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.additional.SkyDimletType;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ISkyRenderHandler;
import org.jetbrains.annotations.Nullable;

public class RFToolsDimensionSpecialEffects extends DimensionSpecialEffects {

    private final ISkyRenderHandler blackSky = new BlackSkyRenderer();
    private final ISkyRenderHandler infernalSky = new TexturedSkyRenderer(new ResourceLocation(RFToolsDim.MODID, "textures/sky/redlines.png"));
    private final ISkyRenderHandler starsSky = new SkyboxRenderer(
            new ResourceLocation(RFToolsDim.MODID, "textures/sky/stars1.png"),
            new ResourceLocation(RFToolsDim.MODID, "textures/sky/stars1a.png"));
    private final ISkyRenderHandler nebulaSky = new SkyboxRenderer(
            new ResourceLocation(RFToolsDim.MODID, "textures/sky/stars3.png"),
            new ResourceLocation(RFToolsDim.MODID, "textures/sky/stars3a.png"));

    private static final Vec3 RED_FOG = new Vec3(1, 0, 0);
    private static final Vec3 BLACK_FOG = new Vec3(0, 0, 0);
    private static final Vec3 WHITE_FOG = new Vec3(1, 1, 1);

    public RFToolsDimensionSpecialEffects() {
        super(192.0f, true, SkyType.NORMAL, false, false);
    }

    private static long cachedSkyMask = -1L;

    public static void clearCache() {
        cachedSkyMask = -1L;
    }

    @Nullable
    @Override
    public ISkyRenderHandler getSkyRenderHandler() {
        long skyMask = getSkyMask();
        if (SkyDimletType.BLACK.match(skyMask)) {
            return blackSky;
        } else if (SkyDimletType.INFERNAL.match(skyMask)) {
            return infernalSky;
        } else if (SkyDimletType.STARS.match(skyMask)) {
            return starsSky;
        } else if (SkyDimletType.NEBULA.match(skyMask)) {
            return nebulaSky;
        } else {
            return null;
        }
    }

    @Override
    public SkyType skyType() {
        long skyMask = getSkyMask();
        if (SkyDimletType.END.match(skyMask)) {
            return SkyType.END;
        }
        return SkyType.NORMAL;
    }

    @Override
    public float getCloudHeight() {
        long skyMask = getSkyMask();
        if (SkyDimletType.NOCLOUDS.match(skyMask)) {
            return Float.NaN;
        } else {
            return super.getCloudHeight();
        }
    }

    private long getSkyMask() {
        if (cachedSkyMask == -1L) {
            ClientDimensionData.ClientData clientData = ClientDimensionData.get().getClientData(Minecraft.getInstance().level.dimension().location());
            cachedSkyMask = clientData.skyDimletTypes();
        }
        return cachedSkyMask;
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 vec, float bright) {
//        ClientDimensionData.ClientData clientData = ClientDimensionData.get().getClientData(Minecraft.getInstance().level.dimension().location());
//        float factor = (float) clientData.power() / clientData.max();
//        Vec3 result = vec.multiply(bright * 0.94F + 0.06F, bright * 0.94F + 0.06F, bright * 0.91F + 0.09F);
//        if (factor < .1) {
//            return result.multiply(factor*10, factor*10, factor*10);
//        }
        long skyMask = getSkyMask();
        if (SkyDimletType.REDFOG.match(skyMask)) {
            return RED_FOG;
        } else if (SkyDimletType.BLACKFOG.match(skyMask)) {
            return BLACK_FOG;
        } else if (SkyDimletType.WHITEFOG.match(skyMask)) {
            return WHITE_FOG;
        } else {
            return vec.multiply(bright * 0.94F + 0.06F, bright * 0.94F + 0.06F, bright * 0.91F + 0.09F);
        }
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        long skyMask = getSkyMask();
        return SkyDimletType.REDFOG.match(skyMask) || SkyDimletType.BLACKFOG.match(skyMask) || SkyDimletType.WHITEFOG.match(skyMask);
    }
}
