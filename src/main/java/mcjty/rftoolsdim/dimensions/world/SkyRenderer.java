package mcjty.rftoolsdim.dimensions.world;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.description.CelestialBodyDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.types.Patreons;
import mcjty.rftoolsdim.dimensions.types.SkyType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class SkyRenderer {
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation locationPlasmaSkyPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/plasmasky.png");
    private static final ResourceLocation locationStars1 = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars1.png");
    private static final ResourceLocation locationStars1a = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars1a.png");
    private static final ResourceLocation locationStars2 = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars2.png");
    private static final ResourceLocation locationStars3 = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars3.png");
    private static final ResourceLocation locationStars3a = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/stars3a.png");
//    private static final ResourceLocation locationDebugSkyPng = new ResourceLocation(RFTools.MODID + ":" +"textures/sky/debugsky.png");

    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation locationSickSunPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/sicksun.png");
    private static final ResourceLocation locationSickMoonPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/sickmoon.png");
    private static final ResourceLocation locationRabbitSunPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/rabbitsun.png");
    private static final ResourceLocation locationRabbitMoonPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/rabbitmoon.png");
    private static final ResourceLocation locationPlanetPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/planet1.png");
    private static final ResourceLocation locationWolfMoonPng = new ResourceLocation(RFToolsDim.MODID + ":" +"textures/sky/wolfred.png");

    private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");

    private static boolean initialized = false;
    /** The star GL Call list */
    private static int starGLCallList;
    /** OpenGL sky list */
    private static int glSkyList;
    /** OpenGL sky list 2 */
    private static int glSkyList2;

    private static void initialize() {
        if (!initialized) {
            initialized = true;

            // @todo VBO

            starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GL11.glNewList(starGLCallList, GL11.GL_COMPILE);
            renderStars();
            GL11.glEndList();
            GlStateManager.popMatrix();

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer renderer = tessellator.getWorldRenderer();
            glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(glSkyList, GL11.GL_COMPILE);
            byte b2 = 64;
            int i = 256 / b2 + 2;
            float f = 16.0F;
            int j;
            int k;

            for (j = -b2 * i; j <= b2 * i; j += b2) {
                for (k = -b2 * i; k <= b2 * i; k += b2) {
                    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                    renderer.pos((j + 0), f, (k + 0)).endVertex();
                    renderer.pos((j + b2), f, (k + 0)).endVertex();
                    renderer.pos((j + b2), f, (k + b2)).endVertex();
                    renderer.pos((j + 0), f, (k + b2)).endVertex();
                    tessellator.draw();
                }
            }

            GL11.glEndList();

            glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(glSkyList2, GL11.GL_COMPILE);
            f = -16.0F;
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

            for (j = -b2 * i; j <= b2 * i; j += b2) {
                for (k = -b2 * i; k <= b2 * i; k += b2) {
                    renderer.pos((j + b2), f, (k + 0)).endVertex();
                    renderer.pos((j + 0), f, (k + 0)).endVertex();
                    renderer.pos((j + 0), f, (k + b2)).endVertex();
                    renderer.pos((j + b2), f, (k + b2)).endVertex();
                }
            }

            tessellator.draw();
            GL11.glEndList();

        }
    }

    public static void registerNoSky(GenericWorldProvider provider) {
        provider.setSkyRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
            }
        });
        provider.setCloudRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
            }
        });
    }

    public static void registerEnderSky(GenericWorldProvider provider) {
        provider.setSkyRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
                SkyRenderer.renderEnderSky();
            }
        });
        provider.setCloudRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
            }
        });
    }

    public static void registerCloudRenderer(final GenericWorldProvider provider, final DimensionInformation information) {
        provider.setCloudRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
                renderClouds(provider, information, partialTicks);
            }
        });
    }

    private static final int SKYTYPE_DARKTOP = 0;
    private static final int SKYTYPE_ALLHORIZONTAL = 1;
    private static final int SKYTYPE_ALL = 2;
    private static final int SKYTYPE_ALTERNATING = 3;

    public static void registerSkybox(GenericWorldProvider provider, final SkyType skyType) {
        provider.setSkyRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
                ResourceLocation sky;
                ResourceLocation sky2 = null;
                int type = SKYTYPE_DARKTOP;
                switch (skyType) {
                    case SKY_INFERNO:
                        sky = locationPlasmaSkyPng;
                        type = SKYTYPE_DARKTOP;
                        break;
                    case SKY_STARS1:
                        sky = locationStars1;
                        sky2 = locationStars1a;
                        type = SKYTYPE_ALTERNATING;
                        break;
                    case SKY_STARS2:
                        sky = locationStars2;
                        type = SKYTYPE_ALL;
                        break;
                    case SKY_STARS3:
                        sky = locationStars3;
                        sky2 = locationStars3a;
                        type = SKYTYPE_ALLHORIZONTAL;
                        break;
                    default:
                        return;
                }
                SkyRenderer.renderSkyTexture(sky, sky2, type);
            }
        });
        provider.setCloudRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
            }
        });
    }

    public static void registerSky(GenericWorldProvider provider, final DimensionInformation information) {
        provider.setSkyRenderer(new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
                SkyRenderer.renderSky(partialTicks, information);
            }
        });
    }

    private static class UV {
        private final double u;
        private final double v;

        private UV(double u, double v) {
            this.u = u;
            this.v = v;
        }

        public static UV uv(double u, double v) {
            return new UV(u, v);
        }
    }


    private static UV[] faceDown  = new UV[] { UV.uv(0.0D, 1.0D), UV.uv(0.0D, 0.0D), UV.uv(1.0D, 0.0D), UV.uv(1.0D, 1.0D) };
    private static UV[] faceUp    = new UV[] { UV.uv(0.0D, 1.0D), UV.uv(0.0D, 0.0D), UV.uv(1.0D, 0.0D), UV.uv(1.0D, 1.0D) };
    private static UV[] faceNorth = new UV[] { UV.uv(0.0D, 0.0D), UV.uv(0.0D, 1.0D), UV.uv(1.0D, 1.0D), UV.uv(1.0D, 0.0D) };
    private static UV[] faceSouth = new UV[] { UV.uv(1.0D, 1.0D), UV.uv(1.0D, 0.0D), UV.uv(0.0D, 0.0D), UV.uv(0.0D, 1.0D) };
    private static UV[] faceWest  = new UV[] { UV.uv(1.0D, 0.0D), UV.uv(0.0D, 0.0D), UV.uv(0.0D, 1.0D), UV.uv(1.0D, 1.0D) };
    private static UV[] faceEast  = new UV[] { UV.uv(0.0D, 1.0D), UV.uv(1.0D, 1.0D), UV.uv(1.0D, 0.0D), UV.uv(0.0D, 0.0D) };

    @SideOnly(Side.CLIENT)
    private static void renderSkyTexture(ResourceLocation sky, ResourceLocation sky2, int type) {
        TextureManager renderEngine = Minecraft.getMinecraft().getTextureManager();

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        for (int i = 0; i < 6; ++i) {
            GlStateManager.pushMatrix();

            UV[] uv = faceDown;
            boolean white = true;

            if (i == 0) {       // Down face
                uv = faceDown;
                switch (type) {
                    case SKYTYPE_ALL:
                        renderEngine.bindTexture(sky);
                        break;
                    case SKYTYPE_ALLHORIZONTAL:
                    case SKYTYPE_ALTERNATING:
                        renderEngine.bindTexture(sky2);
                        break;
                    default:
                        white = false;
                        break;
                }
            } else if (i == 1) {       // North face
                renderEngine.bindTexture(sky);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                uv = faceNorth;
            } else if (i == 2) {       // South face
                renderEngine.bindTexture(sky);
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                uv = faceSouth;
            } else if (i == 3) {       // Up face
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                uv = faceUp;
                switch (type) {
                    case SKYTYPE_ALL:
                        renderEngine.bindTexture(sky);
                        break;
                    case SKYTYPE_ALLHORIZONTAL:
                    case SKYTYPE_ALTERNATING:
                        renderEngine.bindTexture(sky2);
                        break;
                    default:
                        white = false;
                        break;
                }
            } else if (i == 4) {       // East face
                if (type == SKYTYPE_ALTERNATING && sky2 != null) {
                    renderEngine.bindTexture(sky2);
                } else {
                    renderEngine.bindTexture(sky);
                }
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                uv = faceEast;
            } else if (i == 5) {       // West face
                if (type == SKYTYPE_ALTERNATING && sky2 != null) {
                    renderEngine.bindTexture(sky2);
                } else {
                    renderEngine.bindTexture(sky);
                }
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                uv = faceWest;
            }

            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            int cc = white ? 255 : 0;
            renderer.pos(-100.0D, -100.0D, -100.0D).tex(uv[0].u, uv[0].v).color(cc, cc, cc, 255).endVertex();
            renderer.pos(-100.0D, -100.0D, 100.0D).tex(uv[1].u, uv[1].v).color(cc, cc, cc, 255).endVertex();
            renderer.pos(100.0D, -100.0D, 100.0D).tex(uv[2].u, uv[2].v).color(cc, cc, cc, 255).endVertex();
            renderer.pos(100.0D, -100.0D, -100.0D).tex(uv[3].u, uv[3].v).color(cc, cc, cc, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }

    @SideOnly(Side.CLIENT)
    private static void renderEnderSky() {
        TextureManager renderEngine = Minecraft.getMinecraft().getTextureManager();

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);

        renderEngine.bindTexture(locationEndSkyPng);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        for (int i = 0; i < 6; ++i) {
            GlStateManager.pushMatrix();

            if (i == 1) {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 2) {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 3) {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 4) {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (i == 5) {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            int cc = 255; // @todo: decode 2631720 into rgb
            renderer.pos(-100.0D, -100.0D, -100.0D).tex(0, 0).color(cc, cc, cc, 255).endVertex();
            renderer.pos(-100.0D, -100.0D, 100.0D).tex(0, 16).color(cc, cc, cc, 255).endVertex();
            renderer.pos(100.0D, -100.0D, 100.0D).tex(16, 16).color(cc, cc, cc, 255).endVertex();
            renderer.pos(100.0D, -100.0D, -100.0D).tex(16, 0).color(cc, cc, cc, 255).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }

    /**
     * Renders the sky with the partial tick time. Args: partialTickTime
     */
    @SideOnly(Side.CLIENT)
    private static void renderSky(float partialTickTime, DimensionInformation information) {
        initialize();

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        WorldClient world = Minecraft.getMinecraft().theWorld;
        TextureManager renderEngine = Minecraft.getMinecraft().getTextureManager();

        GlStateManager.disableTexture2D();
        Vec3 vec3 = world.getSkyColor(player, partialTickTime);
        float skyRed = (float) vec3.xCoord;
        float skyGreen = (float) vec3.yCoord;
        float skyBlue = (float) vec3.zCoord;

        boolean anaglyph = Minecraft.getMinecraft().gameSettings.anaglyph;
        if (anaglyph) {
            float f4 = (skyRed * 30.0F + skyGreen * 59.0F + skyBlue * 11.0F) / 100.0F;
            float f5 = (skyRed * 30.0F + skyGreen * 70.0F) / 100.0F;
            float f6 = (skyRed * 30.0F + skyBlue * 70.0F) / 100.0F;
            skyRed = f4;
            skyGreen = f5;
            skyBlue = f6;
        }

        GlStateManager.color(skyRed, skyGreen, skyBlue);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(skyRed, skyGreen, skyBlue);

        // @todo support VBO?
//        if (OpenGlHelper.useVbo()) {
//            skyVBO.bindBuffer();
//            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//            GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
//            this.skyVBO.drawArrays(7);
//            this.skyVBO.unbindBuffer();
//            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//
//        } else {
        GlStateManager.callList(glSkyList);
//        }


        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float[] sunsetColors = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTickTime), partialTickTime);

        if (sunsetColors != null) {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTickTime)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float f6 = sunsetColors[0];
            float f7 = sunsetColors[1];
            float f8 = sunsetColors[2];

            if (anaglyph) {
                float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                f6 = f9;
                f7 = f10;
                f8 = f11;
            }

            renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            renderer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, sunsetColors[3]).endVertex();

            for (int j = 0; j <= 16; ++j) {
                float f11 = j * (float) Math.PI * 2.0F / 16.0f;
                float f12 = MathHelper.sin(f11);
                float f13 = MathHelper.cos(f11);
                renderer.pos((f12 * 120.0F), (f13 * 120.0F), (-f13 * 40.0F * sunsetColors[3])).color(sunsetColors[0], sunsetColors[1], sunsetColors[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        renderCelestialBodies(partialTickTime, information, world, renderEngine, tessellator);

        GlStateManager.color(0.0F, 0.0F, 0.0F);
        double d0 = player.getPosition().getY() - world.getHorizon();

        if (d0 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);

            // @todo
//            if (this.vboEnabled)
//            {
//                this.sky2VBO.bindBuffer();
//                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
//                this.sky2VBO.drawArrays(7);
//                this.sky2VBO.unbindBuffer();
//                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//            }
//            else
//            {
            GlStateManager.callList(glSkyList2);
//                GlStateManager.callList(this.glSkyList2);
//            }

            GlStateManager.popMatrix();

            float f8 = 1.0F;
            float f9 = -((float) (d0 + 65.0D));
            float f10 = -f8;
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            renderer.pos((-f8), f9, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f9, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f10, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f10, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f10, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f10, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f9, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f9, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f10, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f10, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f9, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f9, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f9, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f9, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f10, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f10, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f10, (-f8)).color(0, 0, 0, 255).endVertex();
            renderer.pos((-f8), f10, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f10, f8).color(0, 0, 0, 255).endVertex();
            renderer.pos(f8, f10, (-f8)).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
        }

        if (world.provider.isSkyColored()) {
            GlStateManager.color(skyRed * 0.2F + 0.04F, skyGreen * 0.2F + 0.04F, skyBlue * 0.6F + 0.1F);
        } else {
            GlStateManager.color(skyRed, skyGreen, skyBlue);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);
        GlStateManager.callList(glSkyList2);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    private static void renderCelestialBodies(float partialTickTime, DimensionInformation information, WorldClient world, TextureManager renderEngine, Tessellator tessellator) {
        List<CelestialBodyDescriptor> celestialBodies = information.getCelestialBodyDescriptors();

        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.pushMatrix();

        float f6 = 1.0F - world.getRainStrength(partialTickTime);
        ResourceLocation sun = getSun(information);
        ResourceLocation moon = getMoon(information);

        WorldRenderer renderer = tessellator.getWorldRenderer();

        if (celestialBodies.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(world.getCelestialAngle(partialTickTime) * 360.0F, 1.0F, 0.0F, 0.0F);

            float f10 = 30.0F;
            renderEngine.bindTexture(sun);
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            renderer.pos((-f10), 100.0D, (-f10)).tex(0.0D, 0.0D).endVertex();
            renderer.pos(f10, 100.0D, (-f10)).tex(1.0D, 0.0D).endVertex();
            renderer.pos(f10, 100.0D, f10).tex(1.0D, 1.0D).endVertex();
            renderer.pos((-f10), 100.0D, f10).tex(0.0D, 1.0D).endVertex();
            tessellator.draw();
            f10 = 20.0F;
            float f14, f15, f16, f17;
            renderEngine.bindTexture(moon);
            if (!moon.equals(locationMoonPhasesPng)) {
                f14 = 0.0f;
                f15 = 0.0f;
                f16 = 1.0f;
                f17 = 1.0f;
            } else {
                int k = world.getMoonPhase();
                int l = k % 4;
                int i1 = k / 4 % 2;
                f14 = (l + 0) / 4.0F;
                f15 = (i1 + 0) / 2.0F;
                f16 = (l + 1) / 4.0F;
                f17 = (i1 + 1) / 2.0F;
            }
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            renderer.pos((-f10), -100.0D, f10).tex(f16, f17).endVertex();
            renderer.pos(f10, -100.0D, f10).tex(f14, f17).endVertex();
            renderer.pos(f10, -100.0D, (-f10)).tex(f14, f15).endVertex();
            renderer.pos((-f10), -100.0D, (-f10)).tex(f16, f15).endVertex();
            tessellator.draw();
        } else {
            Random random = new Random(world.getSeed());
            for (CelestialBodyDescriptor body : celestialBodies) {
                float offset = 0.0f;
                float factor = 1.0f;
                float yangle = -90.0f;
                if (!body.isMain()) {
                    offset = random.nextFloat() * 200.0f;
                    factor = random.nextFloat() * 3.0f;
                    yangle = random.nextFloat() * 180.0f;
                }
                switch (body.getType()) {
                    case BODY_NONE:
                        break;
                    case BODY_SUN:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderSun(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 30.0F, sun);
                        break;
                    case BODY_LARGESUN:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderSun(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 80.0F, sun);
                        break;
                    case BODY_SMALLSUN:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderSun(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 10.0F, sun);
                        break;
                    case BODY_REDSUN:
                        GlStateManager.color(1.0F, 0.0F, 0.0F, f6);
                        renderSun(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 30.0F, sun);
                        break;
                    case BODY_MOON:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderMoon(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 20.0F, moon);
                        break;
                    case BODY_LARGEMOON:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderMoon(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 60.0F, moon);
                        break;
                    case BODY_SMALLMOON:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderMoon(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 10.0F, moon);
                        break;
                    case BODY_REDMOON:
                        GlStateManager.color(1.0F, 0.0F, 0.0F, f6);
                        renderMoon(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 20.0F, moon);
                        break;
                    case BODY_PLANET:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderPlanet(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 10.0F);
                        break;
                    case BODY_LARGEPLANET:
                        GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
                        renderPlanet(partialTickTime, world, renderEngine, tessellator, offset, factor, yangle, 30.0F);
                        break;
                }
            }
        }


        GlStateManager.disableTexture2D();

        float f18 = world.getStarBrightness(partialTickTime) * f6;

        if (f18 > 0.0F) {
            GlStateManager.color(f18, f18, f18, f18);
            // @todo vbo?
//            if (this.vboEnabled)
//            {
//                this.starVBO.bindBuffer();
//                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
//                this.starVBO.drawArrays(7);
//                this.starVBO.unbindBuffer();
//                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//            }
//            else
//            {
            GlStateManager.callList(starGLCallList);
//        }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    private static ResourceLocation getSun(DimensionInformation information) {
        ResourceLocation sun;
        if (information.isPatreonBitSet(Patreons.PATREON_SICKSUN)) {
            sun = locationSickSunPng;
        } else if (information.isPatreonBitSet(Patreons.PATREON_RABBITSUN)) {
            sun = locationRabbitSunPng;
        } else {
            sun = locationSunPng;
        }
        return sun;
    }

    private static ResourceLocation getMoon(DimensionInformation information) {
        ResourceLocation moon;
        if (information.isPatreonBitSet(Patreons.PATREON_SICKMOON)) {
            moon = locationSickMoonPng;
        } else if (information.isPatreonBitSet(Patreons.PATREON_RABBITMOON)) {
            moon = locationRabbitMoonPng;
        } else if (information.isPatreonBitSet(Patreons.PATREON_TOMWOLF)) {
            moon = locationWolfMoonPng;
        } else {
            moon = locationMoonPhasesPng;
        }
        return moon;
    }

    private static void renderMoon(float partialTickTime, WorldClient world, TextureManager renderEngine, Tessellator tessellator, float offset, float factor, float yangle, float size, ResourceLocation moon) {
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yangle, 0.0F, 1.0F, 0.0F);
        float angle = world.provider.calculateCelestialAngle(world.getWorldInfo().getWorldTime(), partialTickTime);
        angle = angle * factor + offset;
        GlStateManager.rotate(angle * 360.0F, 1.0F, 0.0F, 0.0F);

        float f14, f15, f16, f17;
        renderEngine.bindTexture(moon);
        if (!moon.equals(locationMoonPhasesPng)) {
            f14 = 0.0f;
            f15 = 0.0f;
            f16 = 1.0f;
            f17 = 1.0f;
        } else {
            int k = world.getMoonPhase();
            int l = k % 4;
            int i1 = k / 4 % 2;
            f14 = (l + 0) / 4.0F;
            f15 = (i1 + 0) / 2.0F;
            f16 = (l + 1) / 4.0F;
            f17 = (i1 + 1) / 2.0F;
        }
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        renderer.pos((-size), -100.0D, size).tex(f16, f17).endVertex();
        renderer.pos(size, -100.0D, size).tex(f14, f17).endVertex();
        renderer.pos(size, -100.0D, (-size)).tex(f14, f15).endVertex();
        renderer.pos((-size), -100.0D, (-size)).tex(f16, f15).endVertex();
        tessellator.draw();
    }

    private static void renderSun(float partialTickTime, WorldClient world, TextureManager renderEngine, Tessellator tessellator, float offset, float factor, float yangle, float size, ResourceLocation sun) {
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yangle, 0.0F, 1.0F, 0.0F);
        float angle = world.provider.calculateCelestialAngle(world.getWorldInfo().getWorldTime(), partialTickTime);
        angle = angle * factor + offset;
        GlStateManager.rotate(angle * 360.0F, 1.0F, 0.0F, 0.0F);
        renderEngine.bindTexture(sun);
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        renderer.pos((-size), 100.0D, (-size)).tex(0.0D, 0.0D).endVertex();
        renderer.pos(size, 100.0D, (-size)).tex(1.0D, 0.0D).endVertex();
        renderer.pos(size, 100.0D, size).tex(1.0D, 1.0D).endVertex();
        renderer.pos((-size), 100.0D, size).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }

    private static void renderPlanet(float partialTickTime, WorldClient world, TextureManager renderEngine, Tessellator tessellator, float offset, float factor, float yangle, float size) {
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yangle, 0.0F, 1.0F, 0.0F);
        float angle = world.provider.calculateCelestialAngle(world.getWorldInfo().getWorldTime(), partialTickTime);
        angle = angle * factor + offset;
        GlStateManager.rotate(angle * 360.0F, 1.0F, 0.0F, 0.0F);
        renderEngine.bindTexture(locationPlanetPng);
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        renderer.pos((-size), 100.0D, (-size)).tex(0.0D, 0.0D).endVertex();
        renderer.pos(size, 100.0D, (-size)).tex(1.0D, 0.0D).endVertex();
        renderer.pos(size, 100.0D, size).tex(1.0D, 1.0D).endVertex();
        renderer.pos((-size), 100.0D, size).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }

    private static void renderStars() {
        Random random = new Random(10842L);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (int i = 0; i < 1500; ++i) {
            double d0 = (random.nextFloat() * 2.0F - 1.0F);
            double d1 = (random.nextFloat() * 2.0F - 1.0F);
            double d2 = (random.nextFloat() * 2.0F - 1.0F);
            double d3 = (0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j) {
                    double d17 = 0.0D;
                    double d18 = ((j & 2) - 1) * d3;
                    double d19 = ((j + 1 & 2) - 1) * d3;
                    double d20 = d18 * d16 - d19 * d15;
                    double d21 = d19 * d16 + d18 * d15;
                    double d22 = d20 * d12 + d17 * d13;
                    double d23 = d17 * d12 - d20 * d13;
                    double d24 = d23 * d9 - d21 * d10;
                    double d25 = d21 * d9 + d23 * d10;
                    renderer.pos(d5 + d24, d6 + d22, d7 + d25).endVertex();
                }
            }
        }

        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    public static void renderClouds(GenericWorldProvider provider, DimensionInformation information, float partialTicks) {
        GlStateManager.disableCull();
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager renderEngine = mc.getTextureManager();
        float f1 = (float) (mc.getRenderViewEntity().lastTickPosY + (mc.getRenderViewEntity().posY - mc.getRenderViewEntity().lastTickPosY) * partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        float f2 = 12.0F;
        float f3 = 4.0F;
        RenderGlobal renderGlobal = mc.renderGlobal;

        double d0 = (CloudRenderAccessHelper.getCloudTickCounter(renderGlobal) + partialTicks);

        double entityX = mc.getRenderViewEntity().prevPosX + (mc.getRenderViewEntity().posX - mc.getRenderViewEntity().prevPosX) * partialTicks;
        double entityZ = mc.getRenderViewEntity().prevPosZ + (mc.getRenderViewEntity().posZ - mc.getRenderViewEntity().prevPosZ) * partialTicks;

        double d1 = (entityX + d0 * 0.029999999329447746D) / f2;
        double d2 = entityZ / f2 + 0.33000001311302185D;
        float y = provider.getCloudHeight() - f1 + 0.33F;
        int i = MathHelper.floor_double(d1 / 2048.0D);
        int j = MathHelper.floor_double(d2 / 2048.0D);
        d1 -= (i * 2048);
        d2 -= (j * 2048);
        renderEngine.bindTexture(locationCloudsPng);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Vec3 vec3 = provider.getWorld().getCloudColour(partialTicks);
        float red = (float) vec3.xCoord;
        float green = (float) vec3.yCoord;
        float blue = (float) vec3.zCoord;
        float f8;
        float f9;
        float f10;

        if (mc.gameSettings.anaglyph) {
            f8 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            f9 = (red * 30.0F + green * 70.0F) / 100.0F;
            f10 = (red * 30.0F + blue * 70.0F) / 100.0F;
            red = f8;
            green = f9;
            blue = f10;
        }

        f10 = 0.00390625F;
        f8 = MathHelper.floor_double(d1) * f10;
        f9 = MathHelper.floor_double(d2) * f10;
        float f11 = (float) (d1 - MathHelper.floor_double(d1));
        float f12 = (float) (d2 - MathHelper.floor_double(d2));
        byte b0 = 8;
        byte b1 = 4;
        float f13 = 9.765625E-4F;
        GlStateManager.scale(f2, 1.0F, f2);

        float cr = information.getSkyDescriptor().getCloudColorFactorR();
        float cg = information.getSkyDescriptor().getCloudColorFactorG();
        float cb = information.getSkyDescriptor().getCloudColorFactorB();
        boolean randomColors = information.isPatreonBitSet(Patreons.PATREON_KENNEY);

        WorldRenderer renderer = tessellator.getWorldRenderer();

        for (int k = 0; k < 2; ++k) {
            if (k == 0) {
                GlStateManager.colorMask(false, false, false, false);
            } else if (mc.gameSettings.anaglyph) {
                if (EntityRenderer.anaglyphField == 0) {
                    GlStateManager.colorMask(false, true, true, true);
                } else {
                    GlStateManager.colorMask(true, false, false, true);
                }
            } else {
                GlStateManager.colorMask(true, true, true, true);
            }

            for (int l = -b1 + 1; l <= b1; ++l) {
                for (int i1 = -b1 + 1; i1 <= b1; ++i1) {
                    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    float u = (l * b0);
                    float v = (i1 * b0);
                    float x = u - f11;
                    float z = v - f12;
                    if (randomColors) {
//                        cr = (float) ((u % 10.0f) / 10.0f);
//                        cg = (float) (((u + v) % 10.0f) / 10.0f);
//                        cb = (float) ((v % 10.0f) / 10.0f);
                        cr = x % 1.0f;
                        cg = (x+z) % 1.0f;
                        cb = z % 1.0f;
                    }

                    if (y > -f3 - 1.0F) {
                        renderer.pos((x + 0.0F), (y + 0.0F), (z + b0)).tex(((u + 0.0F) * f10 + f8), ((v + b0) * f10 + f9)).color(red * 0.7F * cr, green * 0.7F * cg, blue * 0.7F * cb, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        renderer.pos((x + b0), (y + 0.0F), (z + b0)).tex(((u + b0) * f10 + f8), ((v + b0) * f10 + f9)).color(red * 0.7F * cr, green * 0.7F * cg, blue * 0.7F * cb, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        renderer.pos((x + b0), (y + 0.0F), (z + 0.0F)).tex(((u + b0) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * 0.7F * cr, green * 0.7F * cg, blue * 0.7F * cb, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        renderer.pos((x + 0.0F), (y + 0.0F), (z + 0.0F)).tex(((u + 0.0F) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * 0.7F * cr, green * 0.7F * cg, blue * 0.7F * cb, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (y <= f3 + 1.0F) {
                        renderer.pos((x + 0.0F), (y + f3 - f13), (z + b0)).tex(((u + 0.0F) * f10 + f8), ((v + b0) * f10 + f9)).color(red * cr, green * cg, blue * cb, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        renderer.pos((x + b0), (y + f3 - f13), (z + b0)).tex(((u + b0) * f10 + f8), ((v + b0) * f10 + f9)).color(red * cr, green * cg, blue * cb, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        renderer.pos((x + b0), (y + f3 - f13), (z + 0.0F)).tex(((u + b0) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * cr, green * cg, blue * cb, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        renderer.pos((x + 0.0F), (y + f3 - f13), (z + 0.0F)).tex(((u + 0.0F) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * cr, green * cg, blue * cb, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    int j1;

                    if (l > -1) {
                        for (j1 = 0; j1 < b0; ++j1) {
                            renderer.pos((x + j1 + 0.0F), (y + 0.0F), (z + b0)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + b0) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            renderer.pos((x + j1 + 0.0F), (y + f3), (z + b0)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + b0) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            renderer.pos((x + j1 + 0.0F), (y + f3), (z + 0.0F)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            renderer.pos((x + j1 + 0.0F), (y + 0.0F), (z + 0.0F)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (l <= 1) {
                        for (j1 = 0; j1 < b0; ++j1) {
                            renderer.pos((x + j1 + 1.0F - f13), (y + 0.0F), (z + b0)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + b0) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            renderer.pos((x + j1 + 1.0F - f13), (y + f3), (z + b0)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + b0) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            renderer.pos((x + j1 + 1.0F - f13), (y + f3), (z + 0.0F)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            renderer.pos((x + j1 + 1.0F - f13), (y + 0.0F), (z + 0.0F)).tex(((u + j1 + 0.5F) * f10 + f8), ((v + 0.0F) * f10 + f9)).color(red * 0.9F * cr, green * 0.9F * cg, blue * 0.9F * cb, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (i1 > -1) {
                        for (j1 = 0; j1 < b0; ++j1) {
                            renderer.pos((x + 0.0F), (y + f3), (z + j1 + 0.0F)).tex(((u + 0.0F) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            renderer.pos((x + b0), (y + f3), (z + j1 + 0.0F)).tex(((u + b0) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            renderer.pos((x + b0), (y + 0.0F), (z + j1 + 0.0F)).tex(((u + b0) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            renderer.pos((x + 0.0F), (y + 0.0F), (z + j1 + 0.0F)).tex(((u + 0.0F) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (i1 <= 1) {
                        for (j1 = 0; j1 < b0; ++j1) {
                            renderer.pos((x + 0.0F), (y + f3), (z + j1 + 1.0F - f13)).tex(((u + 0.0F) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            renderer.pos((x + b0), (y + f3), (z + j1 + 1.0F - f13)).tex(((u + b0) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            renderer.pos((x + b0), (y + 0.0F), (z + j1 + 1.0F - f13)).tex(((u + b0) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            renderer.pos((x + 0.0F), (y + 0.0F), (z + j1 + 1.0F - f13)).tex(((u + 0.0F) * f10 + f8), ((v + j1 + 0.5F) * f10 + f9)).color(red * 0.8F * cr, green * 0.8F * cg, blue * 0.8F * cb, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }

                    tessellator.draw();
                }
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }


}
