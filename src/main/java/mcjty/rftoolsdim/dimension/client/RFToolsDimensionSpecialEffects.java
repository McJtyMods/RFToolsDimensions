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
    private final ISkyRenderHandler plasmaSky = new TexturedSkyRenderer(new ResourceLocation(RFToolsDim.MODID, "textures/sky/redlines.png"));

    public RFToolsDimensionSpecialEffects() {
        super(192.0f, true, SkyType.NORMAL, false, false);
    }

    @Nullable
    @Override
    public ISkyRenderHandler getSkyRenderHandler() {
        long skyMask = getSkyMask();
        if (SkyDimletType.BLACK.match(skyMask)) {
            return blackSky;
        } else {
            return plasmaSky;
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
        ClientDimensionData.ClientData clientData = ClientDimensionData.get().getClientData(Minecraft.getInstance().level.dimension().location());
        return clientData.skyDimletTypes();
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 vec, float bright) {
        return vec.multiply(bright * 0.94F + 0.06F, bright * 0.94F + 0.06F, bright * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }
}
