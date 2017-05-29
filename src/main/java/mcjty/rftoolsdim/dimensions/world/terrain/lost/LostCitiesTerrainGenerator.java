package mcjty.rftoolsdim.dimensions.world.terrain.lost;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.world.terrain.BaseTerrainGenerator;
import mcjty.rftoolsdim.dimensions.world.terrain.NormalTerrainGenerator;
import mcjty.rftoolsdim.varia.GeometryTools;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.*;

public class LostCitiesTerrainGenerator extends NormalTerrainGenerator {

    private final byte groundLevel;
    private final byte waterLevel;
    private IBlockState bedrock;
    private IBlockState air;
    private IBlockState baseBlock;
    private IBlockState baseLiquid;

    private Style style;

    public static final ResourceLocation LOOT = new ResourceLocation(RFToolsDim.MODID, "chests/lostcitychest");
    private static final int STREETBORDER = 3;


    public LostCitiesTerrainGenerator() {
        super();
        this.groundLevel = 63;
        this.waterLevel = 63 - 8;
    }


    private static Map<Character, IBlockState> mapping = null;
    private static List<GenInfo> genInfos = null;

    public static Map<Character, IBlockState> getMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
            mapping.put('#', Blocks.STONEBRICK.getDefaultState());
            mapping.put('=', Blocks.GLASS.getDefaultState());
            mapping.put('@', Blocks.GRAVEL.getDefaultState());      // Will be replaced with glass
            mapping.put(' ', Blocks.AIR.getDefaultState());
            mapping.put('x', Blocks.STONEBRICK.getDefaultState());
            mapping.put('l', Blocks.LADDER.getDefaultState());
            mapping.put('1', Blocks.PLANKS.getDefaultState());      // Monster spawner 1
            mapping.put('2', Blocks.PLANKS.getDefaultState());      // Monster spawner 2
            mapping.put('C', Blocks.PLANKS.getDefaultState());      // Chest
            mapping.put('F', Blocks.PLANKS.getDefaultState());      // Random feature
            mapping.put(':', Blocks.IRON_BARS.getDefaultState());
            mapping.put('D', Blocks.DIRT.getDefaultState());
            mapping.put('G', Blocks.GRASS.getDefaultState());
            mapping.put('p', Blocks.SAPLING.getDefaultState());
            mapping.put('*', Blocks.FLOWER_POT.getDefaultState());
            mapping.put('X', Blocks.MONSTER_EGG.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONEBRICK));
            mapping.put('Q', Blocks.QUARTZ_BLOCK.getDefaultState());
            mapping.put('L', Blocks.BOOKSHELF.getDefaultState());
            mapping.put('W', Blocks.WATER.getDefaultState());
            mapping.put('w', Blocks.COBBLESTONE_WALL.getDefaultState());
            mapping.put('_', Blocks.STONE_SLAB2.getDefaultState());
        }
        return mapping;
    }

    public static List<GenInfo> getGenInfos() {
        if (genInfos == null) {
            genInfos = new ArrayList<>();
            for (int i = 0; i < LostCityData.FLOORS.length; i++) {
                GenInfo gi = new GenInfo();
                LostCityData.Level level = LostCityData.FLOORS[i];
                for (int y = 0; y < 6; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            Character c = level.getC(x, y, z);
                            if (c == '1') {
                                gi.addSpawnerType1(new BlockPos(x, y, z));
                            } else if (c == '2') {
                                gi.addSpawnerType2(new BlockPos(x, y, z));
                            } else if (c == 'C') {
                                gi.addChest(new BlockPos(x, y, z));
                            } else if (c == 'F') {
                                gi.addRandomFeatures(new BlockPos(x, y, z));
                            }
                        }
                    }
                }
                genInfos.add(gi);
            }
        }
        return genInfos;
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        baseBlock = provider.dimensionInformation.getBaseBlockForTerrain();
        baseLiquid = provider.dimensionInformation.getFluidForTerrain().getDefaultState();

        BuildingInfo info = new BuildingInfo(chunkX, chunkZ, provider.seed);

        DamageArea damageArea = new DamageArea(provider.seed, chunkX, chunkZ);
        air = Blocks.AIR.getDefaultState();
        bedrock = Blocks.BEDROCK.getDefaultState();

        if (info.isCity) {
            doCityChunk(chunkX, chunkZ, primer, info, damageArea);
        } else {
            doNormalChunk(chunkX, chunkZ, primer, damageArea);
        }
    }

    private void doNormalChunk(int chunkX, int chunkZ, ChunkPrimer primer, DamageArea damageArea) {
        int cx = chunkX * 16;
        int cz = chunkZ * 16;

        Style style = new Style();
        style.bricks = baseBlock;

        generateHeightmap(chunkX * 4, 0, chunkZ * 4);
        for (int x4 = 0; x4 < 4; ++x4) {
            int l = x4 * 5;
            int i1 = (x4 + 1) * 5;

            for (int z4 = 0; z4 < 4; ++z4) {
                int k1 = (l + z4) * 33;
                int l1 = (l + z4 + 1) * 33;
                int i2 = (i1 + z4) * 33;
                int j2 = (i1 + z4 + 1) * 33;

                for (int height32 = 0; height32 < 32; ++height32) {
                    double d1 = heightMap[k1 + height32];
                    double d2 = heightMap[l1 + height32];
                    double d3 = heightMap[i2 + height32];
                    double d4 = heightMap[j2 + height32];
                    double d5 = (heightMap[k1 + height32 + 1] - d1) * 0.125D;
                    double d6 = (heightMap[l1 + height32 + 1] - d2) * 0.125D;
                    double d7 = (heightMap[i2 + height32 + 1] - d3) * 0.125D;
                    double d8 = (heightMap[j2 + height32 + 1] - d4) * 0.125D;

                    for (int h = 0; h < 8; ++h) {
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;
                        int height = (height32 * 8) + h;

                        for (int x = 0; x < 4; ++x) {
                            int index = ((x + (x4 * 4)) << 12) | ((0 + (z4 * 4)) << 8) | height;
                            short maxheight = 256;
                            index -= maxheight;
                            double d16 = (d11 - d10) * 0.25D;
                            double d15 = d10 - d16;

                            for (int z = 0; z < 4; ++z) {
                                index += maxheight;
                                if ((d15 += d16) > 0.0D) {
                                    IBlockState b = damageArea.damageBlock(baseBlock, height < waterLevel ? baseLiquid : air, provider.rand, damageArea.getDamage(cx + (x4 * 4) + x, height, cz + (z4 * 4) + z), index, style);
                                    BaseTerrainGenerator.setBlockState(primer, index, b);
                                    // @todo find a way to support this 127 feature
//                                    if (baseMeta == 127) {
//                                        realMeta = (byte)((height/2 + x/2 + z/2) & 0xf);
//                                    } else {
//                                        realMeta = baseMeta;
//                                    }
                                } else if (height < waterLevel) {
                                    BaseTerrainGenerator.setBlockState(primer, index, baseLiquid);
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

        List<GeometryTools.AxisAlignedBB2D> boxes = new ArrayList<>();
        for (int x = -1 ; x <= 1 ; x++) {
            for (int z = -1 ; z <= 1 ; z++) {
                if (x != 0 || z != 0) {
                    int ccx = chunkX + x;
                    int ccz = chunkZ + z;
                    BuildingInfo info2 = new BuildingInfo(ccx, ccz, provider.seed);
                    if (info2.isCity) {
                        boxes.add(new GeometryTools.AxisAlignedBB2D(ccx*16, ccz*16, ccx*16+15, ccz*16+15));
                    }
                }
            }
        }
        if (!boxes.isEmpty()) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    double mindist = 1000000000.0;
                    for (GeometryTools.AxisAlignedBB2D box : boxes) {
                        double dist = GeometryTools.squaredDistanceBoxPoint(box, cx + x, cz + z);
                        if (dist < mindist) {
                            mindist = dist;
                        }
                    }
                    int offset = (int) (Math.sqrt(mindist) * 2);
                    flattenChunkBorder(primer, x, offset, z, provider.rand, damageArea, cx, cz);
                }
            }
        }
    }

    private void flattenChunkBorder(ChunkPrimer primer, int x, int offset, int z, Random rand, DamageArea damageArea, int cx, int cz) {
        int index = (x << 12) | (z << 8);
        for (int y = 0; y <= (groundLevel - offset - rand.nextInt(3)) ; y++) {
            IBlockState b = BaseTerrainGenerator.getBlockState(primer, index);
            if (b != bedrock) {
                if (b != baseBlock) {
                    b = damageArea.damageBlock(baseBlock, y < waterLevel ? baseLiquid : air, provider.rand, damageArea.getDamage(cx + x, y, cz + z), index, style);
                    BaseTerrainGenerator.setBlockState(primer, index, b);
                }
            }
            index++;
        }
        int r = rand.nextInt(3);
        index = (x << 12) | (z << 8) + groundLevel + offset + r;
        for (int y = groundLevel + offset + 3; y < 256 ; y++) {
            IBlockState b = BaseTerrainGenerator.getBlockState(primer, index);
            if (b != air) {
                BaseTerrainGenerator.setBlockState(primer, index, air);
            }
            index++;
        }
    }

    private void doCityChunk(int chunkX, int chunkZ, ChunkPrimer primer, BuildingInfo info, DamageArea damageArea) {
        setStyle(info);

        boolean building = info.hasBuilding;

        Random rand = new Random(provider.seed * 377 + chunkZ * 341873128712L + chunkX * 132897987541L);
        rand.nextFloat();
        rand.nextFloat();

        int cx = chunkX * 16;
        int cz = chunkZ * 16;
        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {

                int height = 0;
                while (height < WorldgenConfiguration.bedrockLayer) {
                    BaseTerrainGenerator.setBlockState(primer, index++, bedrock);
                    height++;
                }

                while (height < WorldgenConfiguration.bedrockLayer + 30 + rand.nextInt(3)) {
                    BaseTerrainGenerator.setBlockState(primer, index++, baseBlock);
                    height++;
                }

                if (building) {
                    index = generateBuilding(primer, info, damageArea, rand, cx, cz, index, x, z, height);
                } else {
                    index = generateStreet(primer, info, damageArea, rand, cx, cz, index, x, z, height);
                }
            }
        }

        if (building) {
            int buildingtop = 69 + info.floors * 6;
            char a = (char) Block.BLOCK_STATE_IDS.get(air);
            char b1 = (char) Block.BLOCK_STATE_IDS.get(style.bricks);
            char b2 = (char) Block.BLOCK_STATE_IDS.get(style.bricks_cracked);
            char b3 = (char) Block.BLOCK_STATE_IDS.get(style.bricks_mossy);
            char iron = (char) Block.BLOCK_STATE_IDS.get(Blocks.IRON_BARS.getDefaultState());
            for (int i = 0 ; i < 2 ; i++) {
                index = 0;
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        int belowGround = info.floorsBelowGround;
                        int height = groundLevel - belowGround * 6;
                        index += height;
                        while (height < buildingtop + 6) {
                            if (primer.data[index] != a) {
                                if (primer.data[index + 1] == a
                                        && primer.data[index - 1] == a
                                        && (z == 0 || primer.data[index - 256] == a)
                                        && (z == 15 || primer.data[index + 256] == a)
                                        && (x == 0 || primer.data[index - 256 * 16] == a)
                                        && (x == 15 || primer.data[index + 256 * 16] == a)
                                        ) {
                                    primer.data[index] = a;
                                } else if (primer.data[index - 1] == a && damageArea.damaged[index - 1]) {
                                    if (primer.data[index - 1] == b1 || primer.data[index - 1] == b2 || primer.data[index - 1] == b3) {
                                        primer.data[index - 1] = iron;
                                    } else {
                                        primer.data[index] = a;
                                    }
                                }
                            }
                            index++;
                            height++;
                        }
                        int blocks = 256 - height;
                        index += blocks;
                    }
                }
            }
        }
    }

    private int generateStreet(ChunkPrimer primer, BuildingInfo info, DamageArea damageArea, Random rand, int cx, int cz, int index, int x, int z, int height) {
        while (height < groundLevel) {
            BaseTerrainGenerator.setBlockState(primer, index++, height < waterLevel ? baseLiquid : damageArea.damageBlock(baseBlock, height < waterLevel ? baseLiquid : air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
            height++;
        }

        switch (info.streetType) {
            case NORMAL:
                if (isBorder(x, z)) {
                    IBlockState b = baseBlock;
                    if (x <= STREETBORDER && z > STREETBORDER && z < (15 - STREETBORDER) && info.getXmin().doesRoadExtendTo()) {
                        b = style.street;
                    } else if (x >= (15 - STREETBORDER) && z > STREETBORDER && z < (15 - STREETBORDER) && info.getXmax().doesRoadExtendTo()) {
                        b = style.street;
                    } else if (z <= STREETBORDER && x > STREETBORDER && x < (15 - STREETBORDER) && info.getZmin().doesRoadExtendTo()) {
                        b = style.street;
                    } else if (z >= (15 - STREETBORDER) && x > STREETBORDER && x < (15 - STREETBORDER) && info.getZmax().doesRoadExtendTo()) {
                        b = style.street;
                    }
                    BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(b, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
                    height++;
                } else {
                    BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(style.street, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
                    height++;
                }
                break;
            case FULL:
                BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(style.street, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
                break;
            case PARK:
                if (x == 0 || x == 15 || z == 0 || z == 15) {
                    BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(style.street, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
                } else {
                    BaseTerrainGenerator.setBlockState(primer, index++, damageArea.damageBlock(Blocks.GRASS.getDefaultState(), air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
                }
                break;
        }

        if (info.fountainType >= 0) {
            int l = 0;
            LostCityData.Level level = info.streetType == BuildingInfo.StreetType.PARK ? LostCityData.PARKS[info.fountainType] : LostCityData.FOUNTAINS[info.fountainType];
            while (l < level.getFloor().length) {
                IBlockState b = level.get(x, l, z);
                b = damageArea.damageBlock(b, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style);
                BaseTerrainGenerator.setBlockState(primer, index++, b);
                height++;
                l++;
            }
        }
        int blocks = 256 - height;
        BaseTerrainGenerator.setBlockStateRange(primer, index, index + blocks, air);
        index += blocks;
        return index;
    }

    private int generateBuilding(ChunkPrimer primer, BuildingInfo info, DamageArea damageArea, Random rand, int cx, int cz, int index, int x, int z, int height) {
        int belowGround = info.floorsBelowGround;
        int buildingtop = 69 + info.floors * 6;

        while (height < groundLevel - belowGround*6) {
            BaseTerrainGenerator.setBlockState(primer, index++, height < waterLevel ? baseLiquid : damageArea.damageBlock(baseBlock, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style));
            height++;
        }
        while (height < buildingtop) {
            IBlockState b = getBlockForLevel(rand, info, x, z, height);
            b = damageArea.damageBlock(b, height < waterLevel ? baseLiquid : air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style);
            BaseTerrainGenerator.setBlockState(primer, index++, b);
            height++;
        }
        while (height < buildingtop + 6) {
            int f = getFloor(height);
            int floortype = info.topType;
            LostCityData.Level level = LostCityData.TOPS[floortype];
            IBlockState b = level.get(x, f, z);
            b = getReplacementBlock(rand, info, b, false);
            b = damageArea.damageBlock(b, air, rand, damageArea.getDamage(cx + x, height, cz + z), index, style);
            BaseTerrainGenerator.setBlockState(primer, index++, b);
            height++;
        }
        int blocks = 256 - height;
        BaseTerrainGenerator.setBlockStateRange(primer, index, index + blocks, air);
        index += blocks;
        return index;
    }

    private void setStyle(BuildingInfo info) {
        style = new Style();
        style.street = Blocks.DOUBLE_STONE_SLAB.getDefaultState();

        switch (info.glassColor) {
            case 0: style.glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.WHITE); break;
            case 1: style.glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.GRAY); break;
            case 2: style.glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.LIGHT_BLUE); break;
            case 3: style.glass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLUE); break;
            default: style.glass = Blocks.GLASS.getDefaultState(); break;
        }

        style.quartz = Blocks.QUARTZ_BLOCK.getDefaultState();

        switch (info.buildingStyle) {
            case 0:
                style.bricks = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
                style.bricks_cracked = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
                style.bricks_mossy = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
                break;
            case 1:
                style.bricks = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
                style.bricks_cracked = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
                style.bricks_mossy = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
                break;
            case 2:
                style.bricks = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                style.bricks_cracked = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                style.bricks_mossy = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                break;
            default:
                style.bricks = Blocks.STONEBRICK.getDefaultState();
                style.bricks_cracked = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
                style.bricks_mossy = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
                break;
        }
    }

    private IBlockState getBlockForLevel(Random rand, BuildingInfo info, int x, int z, int height) {
        int f = getFloor(height);
        int l = getLevel(height) + info.floorsBelowGround;
        LostCityData.Level level = LostCityData.FLOORS[info.floorTypes[l]];
        IBlockState b = level.get(x, f, z);
        if (x == 0 && z == 8 && f >= 1 && f <= 2 && info.hasConnectionAtX(l)) {
            BuildingInfo info2 = info.getXmin();
            if (info2.hasBuilding && l <= info2.floors + 1) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        } else if (x == 15 && z == 8 && f >= 1 && f <= 2) {
            BuildingInfo info2 = info.getXmax();
            if (info2.hasBuilding && l <= info2.floors + 1 && info2.hasConnectionAtX(l)) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        }
        if (z == 0 && x == 8 && f >= 1 && f <= 2 && info.hasConnectionAtZ(l)) {
            BuildingInfo info2 = info.getZmin();
            if (info2.hasBuilding && l <= info2.floors + 1) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        } else if (z == 15 && x == 8 && f >= 1 && f <= 2) {
            BuildingInfo info2 = info.getZmax();
            if (info2.hasBuilding && l <= info2.floors + 1 && info2.hasConnectionAtZ(l)) {
                b = air;
            } else if (!info2.hasBuilding && l == 0) {
                b = air;
            }
        }
        boolean down = f == 0 && l == 0;

        return getReplacementBlock(rand, info, b, down);
    }

    private IBlockState getReplacementBlock(Random rand, BuildingInfo info, IBlockState b, boolean down) {
        if (b.getBlock() == Blocks.GRAVEL) {
            switch (info.glassType) {
                case 0: b = style.glass; break;
                case 1: b = style.street; break;
                case 2: b = style.bricks; break;
                case 3: b = style.quartz; break;
            }
        } else if (b.getBlock() == Blocks.LADDER && down) {
            b = style.bricks;
        } else if (b.getBlock() == Blocks.GLASS) {
            b = style.glass;
        } else if (b.getBlock() == Blocks.SAPLING) {
            switch (rand.nextInt(11)) {
                case 0:
                case 1:
                case 2:
                    b = Blocks.RED_FLOWER.getDefaultState();
                    break;
                case 3:
                case 4:
                case 5:
                    b = Blocks.YELLOW_FLOWER.getDefaultState();
                    break;
                case 6:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA);
                    break;
                case 7:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH);
                    break;
                case 8:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK);
                    break;
                case 9:
                    b = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE);
                    break;
                default:
                    b = air;
                    break;
            }
        }

        if (b == style.bricks || b.getBlock() == Blocks.STONEBRICK) {
            b = style.bricks;
            if (rand.nextFloat() < 0.06f) {
                b = style.bricks_cracked;
            } else if (rand.nextFloat() < 0.06f) {
                b = style.bricks_mossy;
            }
        }
        return b;
    }

    public static int getFloor(int height) {
        return (height - 3) % 6;        // -3 instead of -63 because we can also go below the floor
    }

    public static int getLevel(int height) {
        return (height - 63) / 6;
    }

    private boolean isCorner(int x, int z) {
        return (x == 0 && z == 0) || (x == 0 && z == 15) || (x == 15 && z == 0) || (x == 15 && z == 15);
    }

    private boolean isSide(int x, int z) {
        return x == 0 || x == 15 || z == 0 || z == 15;
    }

    private boolean isBorder(int x, int z) {
        return x <= STREETBORDER || x >= (15 - STREETBORDER) || z <= STREETBORDER || z >= (15 - STREETBORDER);
    }


}
