package mcjty.rftoolsdim.dimensions.world;

import mcjty.lib.tools.WorldTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WeightedRandomSelector;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.Patreons;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class GenericWorldGenerator implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(world);
        if (manager.getDimensionDescriptor(world.provider.getDimension()) == null) {
            return; // Not an RFTools dimension
        }

        DimensionInformation information = manager.getDimensionInformation(world.provider.getDimension());
        IBlockState baseBlock = information.getBaseBlockForTerrain();
        if (information.hasFeatureType(FeatureType.FEATURE_OREGEN)) {
            for (IBlockState block : information.getExtraOregen()) {
                addOreSpawn(block, baseBlock, world, random, chunkX * 16, chunkZ * 16, 7, 10, 12, 2, 60);
            }
        }

        Block dimensionalShardBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftools", "dimensional_shard_ore"));
        addOreSpawn(dimensionalShardBlock.getDefaultState(), Blocks.STONE.getDefaultState(), world, random, chunkX * 16, chunkZ * 16,
                    WorldgenConfiguration.oreMinimumVeinSize, WorldgenConfiguration.oreMaximumVeinSize, WorldgenConfiguration.oreMaximumVeinCount,
                    WorldgenConfiguration.oreMinimumHeight, WorldgenConfiguration.oreMaximumHeight);

        if (information.isPatreonBitSet(Patreons.PATREON_PUPPETEER) && Math.abs(chunkX) <= 1 && Math.abs(chunkZ) <= 1) {
            generateBigSpawnPlatform(world, chunkX, chunkZ, puppeteerSpawnPlatform);
        } else if (chunkX == 0 && chunkZ == 0) {
            generateSpawnPlatform(world);
        } else if ((Math.abs(chunkX) > 6 || Math.abs(chunkZ) > 6) && !information.hasFeatureType(FeatureType.FEATURE_NODIMLETBUILDINGS)) {
            // Not too close to starting platform we possibly generate dungeons.
            if (random.nextInt(WorldgenConfiguration.dungeonChance) == 1) {
                generateDimletDungeon(random, chunkX, chunkZ, world);
            }
        }

        if ((Math.abs(chunkX) >= 2 || Math.abs(chunkZ) >= 2) && information.isPatreonBitSet(Patreons.PATREON_COLOREDPRISMS)) {
            if (random.nextInt(10) == 1) {
                generatePrism(chunkX, chunkZ, world);
            }
        }

        if ((Math.abs(chunkX) >= 1 || Math.abs(chunkZ) >= 1) && information.isPatreonBitSet(Patreons.PATREON_PINKPILLARS)) {
            if (random.nextInt(2) == 1) {
                generatePillar(random, chunkX, chunkZ, world);
            }
        }

        if ((Math.abs(chunkX) >= 3 || Math.abs(chunkZ) >= 3) && information.hasFeatureType(FeatureType.FEATURE_VOLCANOES)) {
            if (random.nextInt(WorldgenConfiguration.volcanoChance) == 1) {
                generateVolcano(random, chunkX, chunkZ, world);
            }
        }

    }

    private void generateDimletDungeon(Random random, int chunkX, int chunkZ, World world) {
        int midx = chunkX * 16 + 8;
        int midz = chunkZ * 16 + 8;
        int starty1 = WorldGenerationTools.findSuitableEmptySpot(world, midx - 3, midz - 3);
        int starty2 = WorldGenerationTools.findSuitableEmptySpot(world, midx+3, midz-3);
        int starty3 = WorldGenerationTools.findSuitableEmptySpot(world, midx-3, midz+3);
        int starty4 = WorldGenerationTools.findSuitableEmptySpot(world, midx+3, midz+3);
        int starty = (starty1+starty2+starty3+starty4) / 4;
        if (starty > 1 && starty < world.getHeight()-20) {
            generateDungeon(world, random, midx, starty, midz);
        }
    }

    private void generateVolcano(Random random, int chunkX, int chunkZ, World world) {
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();

        int cntsolid = 0;
        while (y > 3) {
            if (WorldGenerationTools.isSolid(world, x, y, z)) {
                cntsolid++;
                if (cntsolid > 5) {
//                    world.setBlockState(new BlockPos(x, y, z), SpecialSetup.volcanicCoreBlock, 0, 3);
                    Logging.log("Spawned volcano block at " + x + "," + y + "," + z);
                    break;
                }
            } else {
                cntsolid = 0;
            }
            y--;
        }
    }

    private void generatePrism(int chunkX, int chunkZ, World world) {
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        int y = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
        int y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x-7, 0, z-7)).getY();
        if (y1 < y) {
            y = y1;
        }
        y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x+7, 0, z-7)).getY();
        if (y1 < y) {
            y = y1;
        }
        y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x-7, 0, z+7)).getY();
        if (y1 < y) {
            y = y1;
        }
        y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x+7, 0, z+7)).getY();
        if (y1 < y) {
            y = y1;
        }
        if (y > 10 && y < 230) {
            for (int i = 7 ; i >= 0 ; i--) {
                if (i == 0) {
                    setStainedGlassIfAir(world, x, y, z, i);
                } else {
                    for (int j = -i; j <= i - 1; j++) {
                        setStainedGlassIfAir(world, x + j + 1, y, z + i, i);
                        setStainedGlassIfAir(world, x + i, y, z + j, i);
                        setStainedGlassIfAir(world, x - j - 1, y, z - i, i);
                        setStainedGlassIfAir(world, x - i, y, z - j, i);
                    }
                }
                y++;
            }
        }
    }

    private void setStainedGlassIfAir(World world, int x, int y, int z, int i) {
        if (world.isAirBlock(new BlockPos(x, y, z))) {
            world.setBlockState(new BlockPos(x, y, z), Blocks.STAINED_GLASS.getStateFromMeta(i), 2);
        }
    }

    private void generatePillar(Random random, int chunkX, int chunkZ, World world) {
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
        if (y > 10 && y < 240) {
            for (int i = 0 ; i < random.nextInt(3) + 2 ; i++) {
                world.setBlockState(new BlockPos(x, y++, z), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(6), 2);
            }
            world.setBlockState(new BlockPos(x, y, z), Blocks.STAINED_GLASS.getStateFromMeta(6), 2);
        }
    }

    private static int[][] puppeteerSpawnPlatform = new int[][] {
            { -1, -1, -1, -1, -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1 },
            { -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1 },
            { -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1 },
            { -1, -1, 15, 15, 15, 15,  0,  0,  0, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1 },
            { -1, 15, 15, 15, 15,  0,  0,  0,  0,  0, 15, 15, 15,  0, 15, 15, 15,  0, 15, 15, 15, 15, -1, -1 },
            { -1, 15, 15, 15,  0,  0, 15,  0, 15,  0,  0, 15, 15, 15,  0, 15,  0, 15, 15, 15, 15, 15, -1, -1 },
            { 15, 15, 15, 15,  0,  0,  0,  0,  0,  0,  0, 15, 15, 15, 15,  0, 15, 15, 15, 15, 15, 15, 15, -1 },
            { 15, 15, 15, 15,  0,  0, 15,  0, 15,  0,  0, 15, 15, 15,  0, 15,  0, 15, 15, 15, 15, 15, 15, -1 },
            { 15, 15, 15, 15, 15,  0,  0,  0,  0,  0, 15, 15, 15,  0, 15, 15, 15,  0, 15, 15, 15, 15, 15, -1 },
            { 15, 15, 15, 15, 15, 15,  0,  0,  0, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1 },
            { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1 },
            { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -2, 15,  0, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1 },
            { 15, 15, 15, 15, 15, 15, 15, 15,  0, 15,  0, 15,  0, 15,  0, 15, 15, 15, 15, 15, 15, 15, 15, -1 },
            { -1, 15, 15, 15, 15, 15,  0, 15,  0, 15,  0, 15,  0, 15,  0, 15,  0, 15, 15, 15, 15, 15, -1, -1 },
            { -1, 15, 15, 15, 15, 15,  0, 15,  0, 15,  0, 15,  0, 15,  0, 15,  0, 15, 15, 15, 15, 15, -1, -1 },
            { -1, -1, 15, 15, 15, 15,  0, 15,  0, 15,  0, 15,  0, 15,  0, 15,  0, 15, 15, 15, 15, -1, -1, -1 },
            { -1, -1, 15, 15, 15, 15, 15, 15,  0, 15,  0, 15,  0, 15,  0, 15, 15, 15, 15, 15, 15, -1, -1, -1 },
            { -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1 },
            { -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
    };

    private boolean isReceiverPresent(World world, int midx, int midz, int starty, int[][] platform) {
        int r = platform.length;
        int sx = -r / 2;
        int sz = -r / 2;
        for (int x = sx; x < sx + r; x++) {
            for (int z = sz; z < sz + r; z++) {
                int color = platform[r - x - r / 2 - 1][z + r / 2];
                if (color == -2) {
                    if (RFToolsDim.teleportationManager.getReceiverName(world, new BlockPos(x + midx, starty, z + midz)) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void generateBigSpawnPlatform(World world, int chunkX, int chunkZ, int[][] platform) {
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(world.provider.getDimension());

        int midx = 8;
        int midz = 8;
        int starty = WorldGenerationTools.findSuitableEmptySpot(world, midx, midz);
        if (starty == -1) {
            // No suitable spot. We will carve something out.
            starty = 64;
        } else {
            starty++;           // Go one up
        }
        if (isReceiverPresent(world, midx, midz, starty-1, platform)) {
            starty--;
        }

        int r = platform.length;
        int sx = - r/2;
        int sz = - r/2;
        for (int x = sx ; x < sx + r ; x++) {
            int cx = (x + midx) >> 4;
            if (chunkX == cx) {
                for (int z = sz; z < sz + r; z++) {
                    int cz = (z + midz) >> 4;
                    if (chunkZ == cz) {
                        int color = platform[r - x - r / 2 -1][z + r / 2];
                        if (color == -2) {
                            RFToolsDim.teleportationManager.createReceiver(world, new BlockPos(x+midx, starty, z+midz), information.getName(), -1);
                        } else if (color != -1) {
                            world.setBlockState(new BlockPos(x + midx, starty, z + midz), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(color), 2);
                        } else {
                            world.setBlockToAir(new BlockPos(x+midx, starty, z+midz));
                        }
                        for (int y = 1 ; y <= 3 ; y++) {
                            world.setBlockToAir(new BlockPos(x+midx, starty+y, z+midz));
                        }
                    }
                }
            }
        }

        if (chunkX == 0 && chunkZ == 0) {
            registerReceiver(world, dimensionManager, information, midx, midz, starty);
        }
    }

    private void generateSpawnPlatform(World world) {
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(world.provider.getDimension());

        int midx = 8;
        int midz = 8;
        int starty;
        if (information.getTerrainType() == TerrainType.TERRAIN_SOLID) {
            starty = 64;
        } else {
            starty = WorldGenerationTools.findSuitableEmptySpot(world, midx, midz);
        }
        if (starty == -1) {
            // No suitable spot. We will carve something out.
            starty = 64;
        } else {
            starty++;           // Go one up
        }

        boolean shelter = information.isShelter();
        if (information.getTerrainType() == TerrainType.TERRAIN_LIQUID) {
            shelter = true;
        }
        int bounds = 3;
        if (shelter) {
            bounds = 4;
        }

        for (int x = -bounds ; x <= bounds ; x++) {
            for (int z = -bounds ; z <= bounds ; z++) {
                if (x == 0 && z == 0) {
                    RFToolsDim.teleportationManager.createReceiver(world, new BlockPos(x+midx, starty, z+midz), information.getName(), -1);
                } else if (x == 0 && (z == 2 || z == -2)) {
                    world.setBlockState(new BlockPos(x + midx, starty, z + midz), Blocks.GLOWSTONE.getDefaultState(), 3);
                } else {
                    world.setBlockState(new BlockPos(x + midx, starty, z + midz), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
                }
                for (int y = 1 ; y <= 3 ; y++) {
                    world.setBlockToAir(new BlockPos(x+midx, starty+y, z+midz));
                }
                // Check the top layer. If it is something other then air we will replace it with clay as well.
                if (!world.isAirBlock(new BlockPos(x+midx, starty+4, z+midz))) {
                    world.setBlockState(new BlockPos(x + midx, starty + 4, z + midz), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
                }
            }
        }

        if (shelter) {
            for (int y = 1 ; y <= 3 ; y++) {
                for (int x = -bounds ; x <= bounds ; x++) {
                    for (int z = -bounds ; z <= bounds ; z++) {
                        if (x == -bounds || x == bounds || z == -bounds || z == bounds) {
                            if (z == 0 && y >= 2 && y <= 3 || x == 0 && y >= 2 && y <= 3 && z == bounds) {
                                world.setBlockState(new BlockPos(x + midx, starty + y, z + midz), Blocks.GLASS_PANE.getStateFromMeta(0), 2);
                            } else if (x == 0 && y == 1 && z == -bounds) {
                                world.setBlockState(new BlockPos(x + midx, starty + y, z + midz), Blocks.IRON_DOOR.getStateFromMeta(1), 2);
                            } else if (x == 0 && y == 2 && z == -bounds) {
                                world.setBlockState(new BlockPos(x + midx, starty + y, z + midz), Blocks.IRON_DOOR.getStateFromMeta(8), 2);
                            } else {
                                world.setBlockState(new BlockPos(x + midx, starty + y, z + midz), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(9), 2);
                            }
                        }
                    }
                }
            }
            for (int x = -bounds ; x <= bounds ; x++) {
                for (int z = -bounds ; z <= bounds ; z++) {
                    world.setBlockState(new BlockPos(x + midx, starty + 4, z + midz), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(9), 2);
                }
            }
            world.setBlockState(new BlockPos(midx - 1, starty + 2, midz - bounds - 1), Blocks.STONE_BUTTON.getStateFromMeta(4), 2);
            world.setBlockState(new BlockPos(midx + 1, starty + 2, midz - bounds + 1), Blocks.STONE_BUTTON.getStateFromMeta(3), 2);

            world.setBlockState(new BlockPos(midx + 1, starty, midz - bounds - 1), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
            world.setBlockState(new BlockPos(midx, starty, midz - bounds - 1), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
            world.setBlockState(new BlockPos(midx - 1, starty, midz - bounds - 1), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
            world.setBlockState(new BlockPos(midx + 1, starty, midz - bounds - 2), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
            world.setBlockState(new BlockPos(midx, starty, midz - bounds - 2), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
            world.setBlockState(new BlockPos(midx - 1, starty, midz - bounds - 2), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(3), 2);
        }

        registerReceiver(world, dimensionManager, information, midx, midz, starty);
    }

    private void registerReceiver(World world, RfToolsDimensionManager dimensionManager, DimensionInformation information, int midx, int midz, int starty) {
        information.setSpawnPoint(new BlockPos(midx, starty, midz));
        dimensionManager.save(world);
    }

    private void generateDungeon(World world, Random random, int midx, int starty, int midz) {
        boolean doSmallAntenna = random.nextInt(4) == 0;
        boolean doExtraFeature = random.nextInt(4) == 0;

        Block cornerBlock;
        switch (random.nextInt(3)) {
            case 0: cornerBlock = ModBlocks.dimensionalCrossBlock; break;
            case 1: cornerBlock = ModBlocks.dimensionalPattern1Block; break;
            case 2: cornerBlock = ModBlocks.dimensionalPattern2Block; break;
            default: cornerBlock = ModBlocks.dimensionalCross2Block;
        }

        Block buildingBlock = Blocks.STAINED_HARDENED_CLAY;
        int color = random.nextInt(5);
        if (color == 0) {
            color = 3;
        } else if (color == 1) {
            color = 9;
        } else if (color == 2) {
            color = 11;
        } else {
            color = 0;
            buildingBlock = ModBlocks.dimensionalBlankBlock;
        }

        // Spawn the building
        for (int x = midx - 3 ; x  <= midx + 3 ; x++) {
            for (int z = midz - 3 ; z  <= midz + 3 ;z ++) {
                boolean corner = (x == midx-3 || x == midx+3) && (z == midz-3 || z == midz+3);
                boolean xside = x == midx-3 || x == midx+3;
                boolean zside = z == midz-3 || z == midz+3;
                boolean antenna = (x == midx-2 && z == midz-2);
                boolean smallAntenna = doSmallAntenna && (x == midx+2 && z == midz+2);
                world.setBlockState(new BlockPos(x, starty, z), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 2);
                if (corner) {
                    world.setBlockState(new BlockPos(x, starty + 1, z), cornerBlock.getStateFromMeta(1), 2);
                    world.setBlockState(new BlockPos(x, starty + 2, z), cornerBlock.getStateFromMeta(1), 2);
                    world.setBlockState(new BlockPos(x, starty + 3, z), cornerBlock.getStateFromMeta(1), 2);
                } else if (xside) {
                    world.setBlockState(new BlockPos(x, starty + 1, z), buildingBlock.getStateFromMeta(color), 2);
                    if (z >= midz-1 && z <= midz+1) {
                        world.setBlockState(new BlockPos(x, starty + 2, z), Blocks.GLASS_PANE.getStateFromMeta(0), 2);
                    } else {
                        world.setBlockState(new BlockPos(x, starty + 2, z), buildingBlock.getStateFromMeta(color), 2);
                    }
                    world.setBlockState(new BlockPos(x, starty + 3, z), buildingBlock.getStateFromMeta(color), 2);
                } else if (zside) {
                    world.setBlockState(new BlockPos(x, starty + 1, z), buildingBlock.getStateFromMeta(color), 2);
                    world.setBlockState(new BlockPos(x, starty + 2, z), buildingBlock.getStateFromMeta(color), 2);
                    world.setBlockState(new BlockPos(x, starty + 3, z), buildingBlock.getStateFromMeta(color), 2);
                } else {
                    world.setBlockToAir(new BlockPos(x, starty+1, z));
                    world.setBlockToAir(new BlockPos(x, starty+2, z));
                    world.setBlockToAir(new BlockPos(x, starty+3, z));
                }
                if (antenna) {
                    world.setBlockState(new BlockPos(x, starty + 4, z), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 2);
                    world.setBlockState(new BlockPos(x, starty + 5, z), Blocks.IRON_BARS.getDefaultState(), 2);
                    world.setBlockState(new BlockPos(x, starty + 6, z), Blocks.IRON_BARS.getDefaultState(), 2);
                    world.setBlockState(new BlockPos(x, starty + 7, z), Blocks.IRON_BARS.getDefaultState(), 2);
                    world.setBlockState(new BlockPos(x, starty + 8, z), Blocks.GLOWSTONE.getDefaultState(), 3);
                } else if (smallAntenna) {
                    world.setBlockState(new BlockPos(x, starty + 4, z), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 2);
                    world.setBlockState(new BlockPos(x, starty + 5, z), Blocks.IRON_BARS.getDefaultState(), 2);
                    world.setBlockToAir(new BlockPos(x, starty + 6, z));
                    world.setBlockToAir(new BlockPos(x, starty + 7, z));
                    world.setBlockToAir(new BlockPos(x, starty + 8, z));
                } else {
                    world.setBlockState(new BlockPos(x, starty + 4, z), Blocks.STONE_SLAB.getDefaultState(), 2);
                    world.setBlockToAir(new BlockPos(x, starty+5, z));
                    world.setBlockToAir(new BlockPos(x, starty+6, z));
                    world.setBlockToAir(new BlockPos(x, starty+7, z));
                    world.setBlockToAir(new BlockPos(x, starty+8, z));
                }

                // Spawn stone under the building for as long as it is air.
                WorldGenerationTools.fillEmptyWithStone(world, x, starty-1, z);
            }
        }

        if (doExtraFeature) {
            if (!WorldGenerationTools.isSolid(world, midx+4, starty, midz-3)) {
                world.setBlockState(new BlockPos(midx + 4, starty, midz - 3), Blocks.IRON_BARS.getDefaultState(), 2);
            }
            world.setBlockState(new BlockPos(midx + 4, starty + 1, midz - 3), Blocks.IRON_BARS.getDefaultState(), 2);
            world.setBlockState(new BlockPos(midx + 4, starty + 2, midz - 3), Blocks.IRON_BARS.getDefaultState(), 2);
            if (!WorldGenerationTools.isSolid(world, midx+5, starty, midz-3)) {
                world.setBlockState(new BlockPos(midx + 5, starty, midz - 3), buildingBlock.getStateFromMeta(color), 2);
            }
            world.setBlockState(new BlockPos(midx + 5, starty + 1, midz - 3), buildingBlock.getStateFromMeta(color), 2);
            world.setBlockState(new BlockPos(midx + 5, starty + 2, midz - 3), buildingBlock.getStateFromMeta(color), 2);
            WorldGenerationTools.fillEmptyWithStone(world, midx + 4, starty - 1, midz - 3);
            WorldGenerationTools.fillEmptyWithStone(world, midx+5, starty-1, midz-3);
        }

        // Clear the space before the door.
        for (int x = midx-3 ; x <= midx+3 ; x++) {
            for (int y = starty+1 ; y <= starty + 3 ; y++) {
                world.setBlockToAir(new BlockPos(x, y, midz-4));
            }
        }

        // Small platform before the door
        world.setBlockState(new BlockPos(midx - 1, starty, midz - 4), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 2);
        world.setBlockState(new BlockPos(midx, starty, midz - 4), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 2);
        world.setBlockState(new BlockPos(midx + 1, starty, midz - 4), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 2);

        world.setBlockState(new BlockPos(midx, starty + 1, midz - 3), Blocks.IRON_DOOR.getStateFromMeta(1), 2);
        world.setBlockState(new BlockPos(midx, starty + 2, midz - 3), Blocks.IRON_DOOR.getStateFromMeta(8), 2);
        world.setBlockState(new BlockPos(midx - 1, starty + 2, midz - 4), Blocks.STONE_BUTTON.getStateFromMeta(4), 2);
        world.setBlockState(new BlockPos(midx + 1, starty + 2, midz - 2), Blocks.STONE_BUTTON.getStateFromMeta(3), 2);

        world.setBlockState(new BlockPos(midx, starty + 3, midz + 3), Blocks.REDSTONE_LAMP.getDefaultState(), 2);
        world.setBlockState(new BlockPos(midx, starty + 3, midz + 2), Blocks.LEVER.getStateFromMeta(4), 2);

        world.setBlockState(new BlockPos(midx + 2, starty + 1, midz - 2), Blocks.CHEST.getDefaultState(), 2);

        WeightedRandomSelector.Distribution<Integer> bestDistribution = DimletRandomizer.getRandomDimlets().createDistribution(0.2f);

        TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(midx+2, starty+1, midz-2));
        for (int i = 0 ; i < random.nextInt(4)+3 ; i++) {
            ItemStack stack = DimletRandomizer.getRandomPart(random);
            chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), stack);
        }
        if (WorldgenConfiguration.enableDimletsInRFToolsDungeons > 0) {
            for (int i = 0; i < random.nextInt(WorldgenConfiguration.enableDimletsInRFToolsDungeons); i++) {
                DimletKey key = DimletRandomizer.getRandomDimlets().select(bestDistribution, random);
                ItemStack stack = KnownDimletConfiguration.getDimletStack(key);
                chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), stack);
            }
        }

        // Always generate a few cosmetic dimlets
        for (int i = 0; i < WorldgenConfiguration.uncraftableDimletsInRFToolsDungeons; i++) {
            DimletKey key = DimletRandomizer.getRandomUncraftableDimlets().select(bestDistribution, random);
            ItemStack stack = KnownDimletConfiguration.getDimletStack(key);
            chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), stack);
        }

        for (int i = 0 ; i < random.nextInt(2) ; i++) {
            chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.dimletParcelItem));
        }

        EntityItemFrame frame1 = spawnItemFrame(world, midx - 1, starty + 2, midz + 2);
        EntityItemFrame frame2 = spawnItemFrame(world, midx, starty + 2, midz + 2);
        EntityItemFrame frame3 = spawnItemFrame(world, midx + 1, starty + 2, midz + 2);

        if (WorldgenConfiguration.enableDimletsInRFToolsFrames) {
            DimletKey rd1 = DimletRandomizer.getRandomDimlets().select(bestDistribution, random);
            frame1.setDisplayedItem(KnownDimletConfiguration.getDimletStack(rd1));
            DimletKey rd2 = DimletRandomizer.getRandomDimlets().select(bestDistribution, random);
            frame2.setDisplayedItem(KnownDimletConfiguration.getDimletStack(rd2));
            DimletKey rd3 = DimletRandomizer.getRandomDimlets().select(bestDistribution, random);
            frame3.setDisplayedItem(KnownDimletConfiguration.getDimletStack(rd3));
        } else {
            frame1.setDisplayedItem(DimletRandomizer.getRandomPart(random));
            frame2.setDisplayedItem(DimletRandomizer.getRandomPart(random));
            frame3.setDisplayedItem(DimletRandomizer.getRandomPart(random));
        }
    }

    private EntityItemFrame spawnItemFrame(World world, int x, int y, int z) {
        EntityItemFrame frame = new EntityItemFrame(world, new BlockPos(x, y, z+1), EnumFacing.NORTH);
        WorldTools.spawnEntity(world, frame);
        frame.setPosition(x, y, z);
        return frame;
    }

    public void addOreSpawn(IBlockState block, IBlockState targetBlock,
                            World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
        WorldGenMinable minable = new WorldGenMinable(block, (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), p -> p == targetBlock);
        for (int i = 0 ; i < chancesToSpawn ; i++) {
            int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }
}
