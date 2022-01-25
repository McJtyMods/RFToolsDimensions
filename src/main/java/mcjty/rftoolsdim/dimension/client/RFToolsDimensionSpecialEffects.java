package mcjty.rftoolsdim.dimension.client;

import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ISkyRenderHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RFToolsDimensionSpecialEffects extends DimensionSpecialEffects {

    private final ISkyRenderHandler blackSky = new BlackSkyRenderer();

    public RFToolsDimensionSpecialEffects() {
        super(192.0f, true, SkyType.NORMAL, false, false);
    }

    @Nullable
    @Override
    public ISkyRenderHandler getSkyRenderHandler() {
        ClientDimensionData.ClientData clientData = getClientData();
        if (clientData.skyType() == mcjty.rftoolsdim.dimension.additional.SkyType.BLACK) {
            return blackSky;
        } else {
            return null;
        }
    }

    @Override
    public SkyType skyType() {
        ClientDimensionData.ClientData clientData = getClientData();
        return switch (clientData.skyType()) {
            case NORMAL -> SkyType.NORMAL;
            case END -> SkyType.END;
            case BLACK -> SkyType.NONE;
        };
    }

    @NotNull
    private ClientDimensionData.ClientData getClientData() {
        return ClientDimensionData.get().getClientData(Minecraft.getInstance().level.dimension().location());
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
