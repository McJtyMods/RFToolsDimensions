package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.additional.SkyDimletType;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;

public class RFToolsDimensionSpecialEffects extends DimensionSpecialEffects {

    private final BlackSkyRenderer blackSky = new BlackSkyRenderer();
    private final TexturedSkyRenderer infernalSky = new TexturedSkyRenderer(new ResourceLocation(RFToolsDim.MODID, "textures/sky/redlines.png"));
    private final SkyboxRenderer starsSky = new SkyboxRenderer(
            new ResourceLocation(RFToolsDim.MODID, "textures/sky/stars1.png"),
            new ResourceLocation(RFToolsDim.MODID, "textures/sky/stars1a.png"));
    private final SkyboxRenderer nebulaSky = new SkyboxRenderer(
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

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        long skyMask = getSkyMask();
        if (SkyDimletType.BLACK.match(skyMask)) {
            return blackSky.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        } else if (SkyDimletType.INFERNAL.match(skyMask)) {
            return infernalSky.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        } else if (SkyDimletType.STARS.match(skyMask)) {
            return starsSky.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        } else if (SkyDimletType.NEBULA.match(skyMask)) {
            return nebulaSky.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        } else {
            return false;
        }
    }

    @Override
    @Nonnull
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
        long skyMask = getSkyMask();
        if (SkyDimletType.REDFOG.match(skyMask) || SkyDimletType.THICKREDFOG.match(skyMask)) {
            return RED_FOG;
        } else if (SkyDimletType.BLACKFOG.match(skyMask) || SkyDimletType.THICKBLACKFOG.match(skyMask)) {
            return BLACK_FOG;
        } else if (SkyDimletType.WHITEFOG.match(skyMask) || SkyDimletType.THICKWHITEFOG.match(skyMask)) {
            return WHITE_FOG;
        } else {
            return vec.multiply(bright * 0.94F + 0.06F, bright * 0.94F + 0.06F, bright * 0.91F + 0.09F);
        }
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        long skyMask = getSkyMask();
        return SkyDimletType.THICKREDFOG.match(skyMask) || SkyDimletType.THICKBLACKFOG.match(skyMask) || SkyDimletType.THICKWHITEFOG.match(skyMask);
    }
}
