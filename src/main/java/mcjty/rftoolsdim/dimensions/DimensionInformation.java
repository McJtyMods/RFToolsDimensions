package mcjty.rftoolsdim.dimensions;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.api.dimension.IDimensionInformation;
import mcjty.rftoolsdim.compat.ChiselCompat;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.description.*;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.dimlets.types.IDimletType;
import mcjty.rftoolsdim.dimensions.types.*;
import mcjty.rftoolsdim.dimensions.world.BiomeControllerMapping;
import mcjty.rftoolsdim.setup.ModSetup;
import mcjty.rftoolsdim.varia.GenericTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class DimensionInformation implements IDimensionInformation {
    private final DimensionDescriptor descriptor;
    private String name;
    private String ownerName;
    private UUID owner;

    private BlockPos spawnPoint = null;

    private int probeCounter = 0;

    private long forcedDimensionSeed = 0;               // Seed was forced using a seed dimlet.
    private long baseSeed = 0;                          // World seed we start to generate own seed from.

    private int worldVersion = VERSION_OLD;             // Used for compatilibity checking between generated worlds.
    public static final int VERSION_OLD = 0;            // Old version of worlds. Seed is incorrect.
    public static final int VERSION_CORRECTSEED = 1;    // New version of worlds. Seed is correct.
    public static final int VERSION_DIMLETSSEED = 2;    // Newer version of worlds. Seed is correct and based only on the world seed and dimlets.

    private TerrainType terrainType = TerrainType.TERRAIN_VOID;
    private IBlockState baseBlockForTerrain = null;
    private Block fluidForTerrain = null;
    private IBlockState tendrilBlock = null;
    private IBlockState canyonBlock = null;
    private IBlockState[] pyramidBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
    private IBlockState[] sphereBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
    private IBlockState[] hugeSphereBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
    private IBlockState[] scatteredSphereBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
    private IBlockState[] liquidSphereBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
    private Block[] liquidSphereFluids = new Block[] { Blocks.WATER };
    private IBlockState[] hugeLiquidSphereBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
    private Block[] hugeLiquidSphereFluids = new Block[] { Blocks.WATER };
    private IBlockState[] extraOregen = new IBlockState[] {};
    private Block[] fluidsForLakes = new Block[] {};

    private List<MobDescriptor> extraMobs = new ArrayList<>();
    private boolean peaceful = false;
    private boolean noanimals = false;
    private boolean shelter = false;
    private boolean respawnHere = false;
    private boolean cheater = false;

    private Set<FeatureType> featureTypes = EnumSet.noneOf(FeatureType.class);
    private Set<StructureType> structureTypes = EnumSet.noneOf(StructureType.class);
    private Set<EffectType> effectTypes = EnumSet.noneOf(EffectType.class);

    private ControllerType controllerType = null;
    private final List<Biome> biomes = new ArrayList<>();
    private final Map<Integer, Integer> biomeMapping = new HashMap<>();

    private String digitString = "";

    private Float celestialAngle = null;
    private Float timeSpeed = null;

    private SkyDescriptor skyDescriptor;
    private List<CelestialBodyDescriptor> celestialBodyDescriptors;

    // Patreon effects.
    private long patreon1 = 0;

    private String[] dimensionTypes = new String[0];    // Used for Recurrent Complex if that's present.

    private WeatherDescriptor weatherDescriptor;

    // The actual RF cost after taking into account the features we got in our world.
    private int actualRfCost;

    private String injectedDimlets = "";

    public DimensionInformation(String name, DimensionDescriptor descriptor, World world, String playerName, UUID player) {
        this.name = name;
        this.descriptor = descriptor;
        this.ownerName = playerName;
        this.owner = player;

        this.forcedDimensionSeed = descriptor.getForcedSeed();
        long overworldSeed = RfToolsDimensionManager.getWorldForDimension(world, 0).getSeed();
        if (GeneralConfiguration.randomizeSeed) {
            baseSeed = (long) (Math.random() * 10000 + 1);
        } else {
            baseSeed = overworldSeed;
        }

        worldVersion = VERSION_DIMLETSSEED;

        setupFromDescriptor(overworldSeed);
        setupBiomeMapping();

        dump(null);
    }

    private void setupFromDescriptor(long seed) {
        List<Pair<DimletKey,List<DimletKey>>> dimlets = descriptor.getDimletsWithModifiers();
        Random random = new Random(descriptor.calculateSeed(seed));
        actualRfCost = 0;
        DimletType.DIMLET_PATREON.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_TERRAIN.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_FEATURE.dimletType.constructDimension(dimlets, random, this);

        DimletType.DIMLET_STRUCTURE.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_BIOME.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_DIGIT.dimletType.constructDimension(dimlets, random, this);

        DimletType.DIMLET_SKY.dimletType.constructDimension(dimlets, random, this);

        DimletType.DIMLET_MOB.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_SPECIAL.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_TIME.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_EFFECT.dimletType.constructDimension(dimlets, random, this);
        DimletType.DIMLET_WEATHER.dimletType.constructDimension(dimlets, random, this);

        actualRfCost += descriptor.getRfMaintainCost();
    }

    public void injectDimlet(DimletKey key) {
        DimletType type = key.getType();
        IDimletType itype = type.dimletType;
        if (itype.isInjectable(key)) {
            addToCost(key);
            itype.inject(key, this);
            if(injectedDimlets.isEmpty()) {
                injectedDimlets = key.toString();
            } else {
                injectedDimlets = injectedDimlets + "," + key;
            }
        }
    }

    public DimensionInformation(DimensionDescriptor descriptor, NBTTagCompound tagCompound) {
        this.name = tagCompound.getString("name");
        this.ownerName = tagCompound.getString("owner");
        if (tagCompound.hasKey("ownerM")) {
            this.owner = new UUID(tagCompound.getLong("ownerM"), tagCompound.getLong("ownerL"));
        } else {
            this.owner = null;
        }
        this.descriptor = descriptor;

        setSpawnPoint(BlockPosTools.readFromNBT(tagCompound, "spawnPoint"));
        this.probeCounter = tagCompound.getInteger("probeCounter");

        int version = tagCompound.getInteger("version");
        if (version == 1) {
            // This version of the dimension information has the random information persisted.
            setupFromNBT(tagCompound);
        } else {
            // This is an older version. Here we have to calculate the random information again.
            setupFromDescriptor(1);
        }

        setupBiomeMapping();
    }

    public static int[] getIntArraySafe(NBTTagCompound tagCompound, String name) {
        if (!tagCompound.hasKey(name)) {
            return new int[0];
        }
        NBTBase tag = tagCompound.getTag(name);
        if (tag instanceof NBTTagIntArray) {
            return tagCompound.getIntArray(name);
        } else {
            return new int[0];
        }
    }

    private void setupFromNBT(NBTTagCompound tagCompound) {
        terrainType = TerrainType.values()[tagCompound.getInteger("terrain")];
        featureTypes = toEnumSet(getIntArraySafe(tagCompound, "features"), FeatureType.values());
        structureTypes = toEnumSet(getIntArraySafe(tagCompound, "structures"), StructureType.values());
        effectTypes = toEnumSet(getIntArraySafe(tagCompound, "effects"), EffectType.values());

        biomes.clear();
        for (int a : getIntArraySafe(tagCompound, "biomes")) {
            Biome biome = Biome.getBiome(a);
            if (biome != null) {
                biomes.add(biome);
            } else {
                // Protect against deleted biomes (i.e. a mod with biomes gets removed and this dimension still uses it).
                // We will pick a replacement biome here.
                biomes.add(Biomes.PLAINS);
            }
        }
        if (tagCompound.hasKey("controller")) {
            controllerType = ControllerType.values()[tagCompound.getInteger("controller")];
        } else {
            // Support for old type.
            if (biomes.isEmpty()) {
                controllerType = ControllerType.CONTROLLER_DEFAULT;
            } else {
                controllerType = ControllerType.CONTROLLER_SINGLE;
            }
        }

        digitString = tagCompound.getString("digits");

        forcedDimensionSeed = tagCompound.getLong("forcedSeed");
        baseSeed = tagCompound.getLong("baseSeed");
        worldVersion = tagCompound.getInteger("worldVersion");

        baseBlockForTerrain = getBlockMeta(tagCompound, "baseBlock");
        tendrilBlock = getBlockMeta(tagCompound, "tendrilBlock");
        canyonBlock = getBlockMeta(tagCompound, "canyonBlock");
        fluidForTerrain = Block.REGISTRY.getObjectById(tagCompound.getInteger("fluidBlock"));

        hugeLiquidSphereFluids = readFluidsFromNBT(tagCompound, "hugeLiquidSphereFluids");
        hugeLiquidSphereBlocks = readBlockArrayFromNBT(tagCompound, "hugeLiquidSphereBlocks");

        // Support for the old format with only one liquid block.
        Block oldLiquidSphereFluid = Block.REGISTRY.getObjectById(tagCompound.getInteger("liquidSphereFluid"));
        liquidSphereFluids = readFluidsFromNBT(tagCompound, "liquidSphereFluids");
        if (liquidSphereFluids.length == 0) {
            liquidSphereFluids = new Block[] { oldLiquidSphereFluid };
        }

        // Support for the old format with only one sphere block.
        IBlockState oldLiquidSphereBlock = getBlockMeta(tagCompound, "liquidSphereBlock");
        liquidSphereBlocks = readBlockArrayFromNBT(tagCompound, "liquidSphereBlocks");
        if (liquidSphereBlocks.length == 0) {
            liquidSphereBlocks = new IBlockState[] { oldLiquidSphereBlock };
        }

        pyramidBlocks = readBlockArrayFromNBT(tagCompound, "pyramidBlocks");
        if (pyramidBlocks.length == 0) {
            pyramidBlocks = new IBlockState[] { Blocks.STONE.getDefaultState() };
        }

        // Support for the old format with only one sphere block.
        IBlockState oldSphereBlock = getBlockMeta(tagCompound, "sphereBlock");
        sphereBlocks = readBlockArrayFromNBT(tagCompound, "sphereBlocks");
        if (sphereBlocks.length == 0) {
            sphereBlocks = new IBlockState[] { oldSphereBlock };
        }

        hugeSphereBlocks = readBlockArrayFromNBT(tagCompound, "hugeSphereBlocks");
        scatteredSphereBlocks = readBlockArrayFromNBT(tagCompound, "scatteredSphereBlocks");

        extraOregen = readBlockArrayFromNBT(tagCompound, "extraOregen");
        fluidsForLakes = readFluidsFromNBT(tagCompound, "lakeFluids");

        peaceful = tagCompound.getBoolean("peaceful");
        noanimals = tagCompound.getBoolean("noanimals");
        shelter = tagCompound.getBoolean("shelter");
        respawnHere = tagCompound.getBoolean("respawnHere");
        cheater = tagCompound.getBoolean("cheater");
        if (tagCompound.hasKey("celestialAngle")) {
            celestialAngle = tagCompound.getFloat("celestialAngle");
        } else {
            celestialAngle = null;
        }
        if (tagCompound.hasKey("timeSpeed")) {
            timeSpeed = tagCompound.getFloat("timeSpeed");
        } else {
            timeSpeed = null;
        }
        probeCounter = tagCompound.getInteger("probes");
        actualRfCost = tagCompound.getInteger("actualCost");

        injectedDimlets = tagCompound.getString("injectedDimlets");

        skyDescriptor = new SkyDescriptor.Builder().fromNBT(tagCompound).build();
        calculateCelestialBodyDescriptors();

        patreon1 = tagCompound.getLong("patreon1");

        weatherDescriptor = new WeatherDescriptor.Builder().fromNBT(tagCompound).build();

        extraMobs.clear();
        NBTTagList list = tagCompound.getTagList("mobs", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = list.getCompoundTagAt(i);
            String className = tc.getString("class");
            int chance = tc.getInteger("chance");
            int minGroup = tc.getInteger("minGroup");
            int maxGroup = tc.getInteger("maxGroup");
            int maxLoaded = tc.getInteger("maxLoaded");
            Class<? extends EntityLiving> c = null;
            try {
                c = GenericTools.castClass(Class.forName(className), EntityLiving.class);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            MobDescriptor mob = new MobDescriptor(c, chance, minGroup, maxGroup, maxLoaded);
            extraMobs.add(mob);
        }

        String ds = tagCompound.getString("dimensionTypes");
        dimensionTypes = StringUtils.split(ds, ",");
        if (dimensionTypes == null) {
            dimensionTypes = new String[0];
        }
    }

    private Block[] readFluidsFromNBT(NBTTagCompound tagCompound, String name) {
        List<Block> fluids = new ArrayList<>();

        if (tagCompound.hasKey(name + "_reg")) {
            // New system
            NBTTagList list = tagCompound.getTagList(name + "_reg", Constants.NBT.TAG_STRING);
            for (int i = 0 ; i < list.tagCount() ; i++) {
                NBTTagString reg = (NBTTagString) list.get(i);
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(reg.getString()));
                if (block == null) {
                    // Block no longer exists, ignore
                    Logging.logError("Block with id " + reg.getString() + " no longer exists! Ignoring...");
                } else {
                    fluids.add(block);
                }
            }
        } else {
            // Old system
            for (int a : getIntArraySafe(tagCompound, name)) {
                fluids.add(Block.REGISTRY.getObjectById(a));
            }
        }
        return fluids.toArray(new Block[fluids.size()]);
    }

    private static IBlockState[] readBlockArrayFromNBT(NBTTagCompound tagCompound, String name) {
        List<IBlockState> blocks = new ArrayList<>();
        int[] metas = getIntArraySafe(tagCompound, name + "_meta");

        if (tagCompound.hasKey(name + "_reg")) {
            // New system
            NBTTagList list = tagCompound.getTagList(name + "_reg", Constants.NBT.TAG_STRING);
            for (int i = 0 ; i < list.tagCount() ; i++) {
                NBTTagString reg = (NBTTagString) list.get(i);
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(reg.getString()));
                if (block == null) {
                    // Block no longer exists, ignore
                    Logging.logError("Block with id " + reg.getString() + " no longer exists! Ignoring...");
                } else {
                    int meta = 0;
                    if (i < metas.length) {
                        meta = metas[i];
                    }
                    try {
                        blocks.add(block.getStateFromMeta(meta));
                    } catch (Exception e) {
                        Logging.logError("Block with id " + reg.getString() + " no longer supports meta " + meta + "! Ignoring...");
                    }
                }
            }
        } else {
            // Old system
            int[] blockIds = getIntArraySafe(tagCompound, name);
            for (int i = 0; i < blockIds.length; i++) {
                int id = blockIds[i];
                Block block = Block.REGISTRY.getObjectById(id);
                int meta = 0;
                if (i < metas.length) {
                    meta = metas[i];
                }
                try {
                    blocks.add(block.getStateFromMeta(meta));
                } catch (Exception e) {
                    // To work around a problem with changed ids we catch this exception here.
                    // In future this should never happen since we don't persist with ids anymore.
                    // But we were not always as smart as we are now...
                    Logging.logError("Block has changed in dimension: ignoring");
                }
            }
        }
        return blocks.toArray(new IBlockState[blocks.size()]);
    }

    private IBlockState getBlockMeta(NBTTagCompound tagCompound, String name) {
        Block block = Block.REGISTRY.getObjectById(tagCompound.getInteger(name));
        int meta = tagCompound.getInteger(name + "_meta");
        return block.getStateFromMeta(meta);
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("name", getName());
        tagCompound.setString("owner", ownerName);
        if (owner != null) {
            tagCompound.setLong("ownerM", owner.getMostSignificantBits());
            tagCompound.setLong("ownerL", owner.getLeastSignificantBits());
        }

        BlockPos spawnPoint = getSpawnPoint();
        if (spawnPoint != null) {
            BlockPosTools.writeToNBT(tagCompound, "spawnPoint", spawnPoint);
        }
        tagCompound.setInteger("probeCounter", getProbeCounter());
        tagCompound.setInteger("version", 1);           // Version number so that we can detect incompatible changes in persisted dimension information objects.

        tagCompound.setInteger("terrain", terrainType == null ? TerrainType.TERRAIN_VOID.ordinal() : terrainType.ordinal());
        tagCompound.setIntArray("features", toIntArray(featureTypes));
        tagCompound.setIntArray("structures", toIntArray(structureTypes));
        tagCompound.setIntArray("effects", toIntArray(effectTypes));

        List<Integer> c = new ArrayList<>(biomes.size());
        for (Biome t : biomes) {
            if (t != null) {
                c.add(Biome.getIdForBiome(t));
            } else {
                c.add(Biome.getIdForBiome(Biomes.PLAINS));
            }
        }
        tagCompound.setIntArray("biomes", ArrayUtils.toPrimitive(c.toArray(new Integer[c.size()])));
        tagCompound.setInteger("controller", controllerType == null ? ControllerType.CONTROLLER_DEFAULT.ordinal() : controllerType.ordinal());
        tagCompound.setString("digits", digitString);

        tagCompound.setLong("forcedSeed", forcedDimensionSeed);
        tagCompound.setLong("baseSeed", baseSeed);
        tagCompound.setInteger("worldVersion", worldVersion);

        setBlockMeta(tagCompound, baseBlockForTerrain, "baseBlock");
        setBlockMeta(tagCompound, tendrilBlock, "tendrilBlock");

        writeBlocksToNBT(tagCompound, pyramidBlocks, "pyramidBlocks");

        writeBlocksToNBT(tagCompound, sphereBlocks, "sphereBlocks");
        if (sphereBlocks.length > 0) {
            // Write out a single sphere block for compatibility with older RFTools.
            setBlockMeta(tagCompound, sphereBlocks[0], "sphereBlock");
        }

        writeBlocksToNBT(tagCompound, hugeSphereBlocks, "hugeSphereBlocks");
        writeBlocksToNBT(tagCompound, scatteredSphereBlocks, "scatteredSphereBlocks");
        writeBlocksToNBT(tagCompound, hugeLiquidSphereBlocks, "hugeLiquidSphereBlocks");
        writeFluidsToNBT(tagCompound, hugeLiquidSphereFluids, "hugeLiquidSphereFluids");

        writeBlocksToNBT(tagCompound, liquidSphereBlocks, "liquidSphereBlocks");
        if (liquidSphereBlocks.length > 0) {
            // Write out a single sphere block for compatibility with older RFTools.
            setBlockMeta(tagCompound, liquidSphereBlocks[0], "liquidSphereBlock");
        }

        writeFluidsToNBT(tagCompound, liquidSphereFluids, "liquidSphereFluids");
        if (liquidSphereFluids.length > 0) {
            tagCompound.setInteger("liquidSphereFluid", Block.REGISTRY.getIDForObject(liquidSphereFluids[0]));
        }

        setBlockMeta(tagCompound, canyonBlock, "canyonBlock");
        tagCompound.setInteger("fluidBlock", Block.REGISTRY.getIDForObject(fluidForTerrain));

        writeBlocksToNBT(tagCompound, extraOregen, "extraOregen");
        writeFluidsToNBT(tagCompound, fluidsForLakes, "lakeFluids");

        tagCompound.setBoolean("peaceful", peaceful);
        tagCompound.setBoolean("noanimals", noanimals);
        tagCompound.setBoolean("shelter", shelter);
        tagCompound.setBoolean("respawnHere", respawnHere);
        tagCompound.setBoolean("cheater", cheater);
        if (celestialAngle != null) {
            tagCompound.setFloat("celestialAngle", celestialAngle);
        }
        if (timeSpeed != null) {
            tagCompound.setFloat("timeSpeed", timeSpeed);
        }
        tagCompound.setInteger("probes", probeCounter);
        tagCompound.setInteger("actualCost", actualRfCost);

        tagCompound.setString("injectedDimlets", injectedDimlets);

        skyDescriptor.writeToNBT(tagCompound);
        weatherDescriptor.writeToNBT(tagCompound);

        tagCompound.setLong("patreon1", patreon1);

        NBTTagList list = new NBTTagList();
        for (MobDescriptor mob : extraMobs) {
            NBTTagCompound tc = new NBTTagCompound();

            if (mob != null) {
                tc.setString("class", mob.entityClass.getName());
                tc.setInteger("chance", mob.itemWeight);
                tc.setInteger("minGroup", mob.minGroupCount);
                tc.setInteger("maxGroup", mob.maxGroupCount);
                tc.setInteger("maxLoaded", mob.getMaxLoaded());
                list.appendTag(tc);
            }
        }

        tagCompound.setTag("mobs", list);
        tagCompound.setString("dimensionTypes", StringUtils.join(dimensionTypes, ","));
    }

    private void setBlockMeta(NBTTagCompound tagCompound, IBlockState blockMeta, String name) {
        tagCompound.setInteger(name, Block.REGISTRY.getIDForObject(blockMeta.getBlock()));
        // @todo check if this is the good way to persist?
        tagCompound.setInteger(name + "_meta", blockMeta.getBlock().getMetaFromState(blockMeta));
    }

    private static void writeFluidsToNBT(NBTTagCompound tagCompound, Block[] fluids, String name) {
        NBTTagList list = new NBTTagList();
//        List<Integer> c;
//        c = new ArrayList<Integer>(fluids.length);
        for (Block t : fluids) {
//            c.add(Block.REGISTRY.getIDForObject(t));
            list.appendTag(new NBTTagString(t.getRegistryName().toString()));
        }
//        tagCompound.setIntArray(name, ArrayUtils.toPrimitive(c.toArray(new Integer[c.size()])));
        tagCompound.setTag(name + "_reg", list);
    }

    private static void writeBlocksToNBT(NBTTagCompound tagCompound, IBlockState[] blocks, String name) {
        NBTTagList list = new NBTTagList();
//        List<Integer> ids = new ArrayList<Integer>(blocks.length);
        List<Integer> meta = new ArrayList<>(blocks.length);
        for (IBlockState t : blocks) {
//            ids.add(Block.REGISTRY.getIDForObject(t.getBlock()));
            list.appendTag(new NBTTagString(t.getBlock().getRegistryName().toString()));
            meta.add(t.getBlock().getMetaFromState(t));
        }
//        tagCompound.setIntArray(name, ArrayUtils.toPrimitive(ids.toArray(new Integer[ids.size()])));

        tagCompound.setTag(name + "_reg", list);
        tagCompound.setIntArray(name + "_meta", ArrayUtils.toPrimitive(meta.toArray(new Integer[meta.size()])));
    }

    private static <T extends Enum<T>> int[] toIntArray(Collection<T> collection) {
        List<Integer> c = new ArrayList<>(collection.size());
        for (T t : collection) {
            c.add(t.ordinal());
        }
        return ArrayUtils.toPrimitive(c.toArray(new Integer[c.size()]));
    }

    private static <T extends Enum<T>> Set<T> toEnumSet(int[] arr, T[] values) {
        Set<T> list = new HashSet<>();
        for (int a : arr) {
            list.add(values[a]);
        }
        return list;
    }


    private void logDebug(EntityPlayer player, String message) {
        if (player == null) {
            Logging.log(message);
        } else {
            Logging.message(player, TextFormatting.YELLOW + message);
        }
    }

    public static String getDisplayName(Block block) {
        if (block == null) {
            return "null";
        }

        return getDisplayName(block.getDefaultState());
    }

    public static String getDisplayName(IBlockState state) {
        if (state == null) {
            return "null";
        }

        Block block = state.getBlock();

        String suffix = "";
        if ("chisel".equals(block.getRegistryName().getResourceDomain()) && ModSetup.chisel) {
            // Special case for chisel as it has the same name as the base block for all its variants
            String readableName = ChiselCompat.getReadableName(state);
            if (readableName != null) {
                suffix = ", " + readableName;
            }
        }

        if (block instanceof BlockLiquid) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                FluidStack stack = new FluidStack(fluid, 1);
                return stack.getLocalizedName() + suffix;
            }
        }

        Item item = Item.getItemFromBlock(block);
        ItemStack itemStack = new ItemStack(item, 1, item.getHasSubtypes() ? block.getMetaFromState(state) : 0);
        if (itemStack.isEmpty()) {
            return block.getLocalizedName() + suffix;
        }

        return itemStack.getDisplayName() + suffix;
    }

    public void dump(EntityPlayer player) {
        if (!injectedDimlets.isEmpty()) {
            logDebug(player, "    Injected dimlets: " + injectedDimlets);
        }
        String digits = getDigitString();
        if (!digits.isEmpty()) {
            logDebug(player, "    Digits: " + digits);
        }
        if (forcedDimensionSeed != 0) {
            logDebug(player, "    Forced seed: " + forcedDimensionSeed);
        }
        if (baseSeed != 0) {
            logDebug(player, "    Base seed: " + baseSeed);
        }
        logDebug(player, "    World version: " + worldVersion);
        TerrainType terrainType = getTerrainType();
        logDebug(player, "    Terrain: " + terrainType.toString());
        logDebug(player, "        Base block: " + getDisplayName(baseBlockForTerrain));
        if (featureTypes.contains(FeatureType.FEATURE_TENDRILS)) {
            logDebug(player, "        Tendril block: " + getDisplayName(tendrilBlock));
        }
        if (featureTypes.contains(FeatureType.FEATURE_PYRAMIDS)) {
            for (IBlockState block : pyramidBlocks) {
                if (block != null) {
                    logDebug(player, "        Pyramid blocks: " + getDisplayName(block));
                }
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_ORBS)) {
            for (IBlockState block : sphereBlocks) {
                if (block != null) {
                    logDebug(player, "        Orb blocks: " + getDisplayName(block));
                }
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_HUGEORBS)) {
            for (IBlockState block : hugeSphereBlocks) {
                if (block != null) {
                    logDebug(player, "        Huge Orb blocks: " + getDisplayName(block));
                }
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_SCATTEREDORBS)) {
            for (IBlockState block : scatteredSphereBlocks) {
                if (block != null) {
                    logDebug(player, "        Scattered Orb blocks: " + getDisplayName(block));
                }
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_LIQUIDORBS)) {
            for (IBlockState block : liquidSphereBlocks) {
                if (block != null) {
                    logDebug(player, "        Liquid Orb blocks: " + getDisplayName(block));
                }
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_HUGELIQUIDORBS)) {
            for (IBlockState block : hugeLiquidSphereBlocks) {
                if (block != null) {
                    logDebug(player, "        Huge Liquid Orb blocks: " + getDisplayName(block));
                }
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_CANYONS)) {
            logDebug(player, "        Canyon block: " + getDisplayName(canyonBlock));
        }
        logDebug(player, "        Base fluid: " + getDisplayName(fluidForTerrain));
        logDebug(player, "    Biome controller: " + (controllerType == null ? "<null>" : controllerType.name()));
        for (Biome biome : getBiomes()) {
            if (biome != null) {
                logDebug(player, "    Biome: " + biome.biomeName);
            }
        }
        for (FeatureType featureType : getFeatureTypes()) {
            logDebug(player, "    Feature: " + featureType.toString());
        }
        for (IBlockState block : extraOregen) {
            if (block != null) {
                logDebug(player, "        Extra ore: " + getDisplayName(block));
            }
        }
        for (Block block : fluidsForLakes) {
            logDebug(player, "        Lake fluid: " + getDisplayName(block));
        }
        if (featureTypes.contains(FeatureType.FEATURE_LIQUIDORBS)) {
            for (Block fluid : liquidSphereFluids) {
                logDebug(player, "        Liquid orb fluids: " + getDisplayName(fluid));
            }
        }
        if (featureTypes.contains(FeatureType.FEATURE_HUGELIQUIDORBS)) {
            for (Block fluid : hugeLiquidSphereFluids) {
                logDebug(player, "        Huge Liquid orb fluids: " + getDisplayName(fluid));
            }
        }
        for (StructureType structureType : getStructureTypes()) {
            logDebug(player, "    Structure: " + structureType.toString());
        }
//        if (structureTypes.contains(StructureType.STRUCTURE_RECURRENTCOMPLEX)) {
//            for (String type : dimensionTypes) {
//                logDebug(player, "    RR DimensionType: " + type);
//            }
//        }
        for (EffectType effectType : getEffectTypes()) {
            logDebug(player, "    Effect: " + effectType.toString());
        }
        logDebug(player, "    Sun brightness: " + skyDescriptor.getSunBrightnessFactor());
        logDebug(player, "    Star brightness: " + skyDescriptor.getStarBrightnessFactor());
        float r = skyDescriptor.getSkyColorFactorR();
        float g = skyDescriptor.getSkyColorFactorG();
        float b = skyDescriptor.getSkyColorFactorB();
        logDebug(player, "    Sky color: " + r + ", " + g + ", " + b);
        r = skyDescriptor.getFogColorFactorR();
        g = skyDescriptor.getFogColorFactorG();
        b = skyDescriptor.getFogColorFactorB();
        logDebug(player, "    Fog color: " + r + ", " + g + ", " + b);
        r = skyDescriptor.getCloudColorFactorR();
        g = skyDescriptor.getCloudColorFactorG();
        b = skyDescriptor.getCloudColorFactorB();
        logDebug(player, "    Cloud color: " + r + ", " + g + ", " + b);
        SkyType skyType = skyDescriptor.getSkyType();
        if (skyType != SkyType.SKY_NORMAL) {
            logDebug(player, "    Sky type: " + skyType.toString());
        }
        for (CelestialBodyType bodyType : skyDescriptor.getCelestialBodies()) {
            logDebug(player, "    Sky body: " + bodyType.name());
        }

        if (weatherDescriptor.getRainStrength() > -0.5f) {
            logDebug(player, "    Weather rain: " + weatherDescriptor.getRainStrength());
        }
        if (weatherDescriptor.getThunderStrength() > -0.5f) {
            logDebug(player, "    Weather thunder " + weatherDescriptor.getThunderStrength());
        }


        for (MobDescriptor mob : extraMobs) {
            if (mob != null) {
                logDebug(player, "    Mob: " + mob.entityClass.getName());
            }
        }
        if (peaceful) {
            logDebug(player, "    Peaceful mode");
        }
        if (noanimals) {
            logDebug(player, "    No animals mode");
        }
        if (shelter) {
            logDebug(player, "    Safe shelter");
        }
        if (respawnHere) {
            logDebug(player, "    Respawn local");
        }
        if (cheater) {
            logDebug(player, "    CHEATER!");
        }
        if (celestialAngle != null) {
            logDebug(player, "    Celestial angle: " + celestialAngle);
        }
        if (timeSpeed != null) {
            logDebug(player, "    Time speed: " + timeSpeed);
        }
        if (probeCounter > 0) {
            logDebug(player, "    Probes: " + probeCounter);
        }
        if (patreon1 != 0) {
            logDebug(player, "    Patreon: " + patreon1);
        }
    }

    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, ownerName);
        if (owner == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeLong(owner.getMostSignificantBits());
            buf.writeLong(owner.getLeastSignificantBits());
        }
        NetworkTools.writeEnum(buf, terrainType, TerrainType.TERRAIN_VOID);
        NetworkTools.writeEnumCollection(buf, featureTypes);
        NetworkTools.writeEnumCollection(buf, structureTypes);
        NetworkTools.writeEnumCollection(buf, effectTypes);

        buf.writeInt(biomes.size());
        for (Biome entry : biomes) {
            if (entry != null) {
                int id = Biome.getIdForBiome(entry);
                buf.writeInt(id);
            } else {
                buf.writeInt(Biome.getIdForBiome(Biomes.PLAINS));
            }
        }
        NetworkTools.writeEnum(buf, controllerType, ControllerType.CONTROLLER_DEFAULT);

        NetworkTools.writeString(buf, digitString);
        buf.writeLong(forcedDimensionSeed);
        buf.writeLong(baseSeed);
        buf.writeInt(worldVersion);

        buf.writeInt(Block.REGISTRY.getIDForObject(baseBlockForTerrain.getBlock()));
        buf.writeInt(baseBlockForTerrain.getBlock().getMetaFromState(baseBlockForTerrain));
        buf.writeInt(Block.REGISTRY.getIDForObject(tendrilBlock.getBlock()));
        buf.writeInt(tendrilBlock.getBlock().getMetaFromState(tendrilBlock));

        writeBlockArrayToBuf(buf, pyramidBlocks);
        writeBlockArrayToBuf(buf, sphereBlocks);
        writeBlockArrayToBuf(buf, hugeSphereBlocks);
        writeBlockArrayToBuf(buf, scatteredSphereBlocks);
        writeBlockArrayToBuf(buf, liquidSphereBlocks);
        writeFluidArrayToBuf(buf, liquidSphereFluids);
        writeBlockArrayToBuf(buf, hugeLiquidSphereBlocks);
        writeFluidArrayToBuf(buf, hugeLiquidSphereFluids);

        buf.writeInt(Block.REGISTRY.getIDForObject(canyonBlock.getBlock()));
        buf.writeInt(canyonBlock.getBlock().getMetaFromState(canyonBlock));
        buf.writeInt(Block.REGISTRY.getIDForObject(fluidForTerrain));

        writeBlockArrayToBuf(buf, extraOregen);

        writeFluidArrayToBuf(buf, fluidsForLakes);

        buf.writeBoolean(peaceful);
        buf.writeBoolean(noanimals);
        buf.writeBoolean(shelter);
        buf.writeBoolean(respawnHere);
        buf.writeBoolean(cheater);
        NetworkTools.writeFloat(buf, celestialAngle);
        NetworkTools.writeFloat(buf, timeSpeed);

        buf.writeInt(probeCounter);
        buf.writeInt(actualRfCost);

        NetworkTools.writeString(buf, injectedDimlets);

        skyDescriptor.toBytes(buf);
        weatherDescriptor.toBytes(buf);

        buf.writeLong(patreon1);

        int mobsize = 0;
        for (MobDescriptor mob : extraMobs) {
            if (mob != null) {
                mobsize++;
            }
        }
        buf.writeInt(mobsize);
        for (MobDescriptor mob : extraMobs) {
            if (mob != null) {
                NetworkTools.writeString(buf, mob.entityClass.getName());
                buf.writeInt(mob.itemWeight);
                buf.writeInt(mob.minGroupCount);
                buf.writeInt(mob.maxGroupCount);
                buf.writeInt(mob.getMaxLoaded());
            }
        }

        buf.writeInt(dimensionTypes.length);
        for (String type : dimensionTypes) {
            NetworkTools.writeString(buf, type);
        }

    }

    private static void writeFluidArrayToBuf(ByteBuf buf, Block[] fluids) {
        buf.writeInt(fluids.length);
        for (Block block : fluids) {
            buf.writeInt(Block.REGISTRY.getIDForObject(block));
        }
    }

    private static void writeBlockArrayToBuf(ByteBuf buf, IBlockState[] array) {
        buf.writeInt(array.length);
        for (IBlockState block : array) {
            buf.writeInt(Block.REGISTRY.getIDForObject(block.getBlock()));
            buf.writeInt(block.getBlock().getMetaFromState(block));
        }
    }

    public DimensionInformation(String name, DimensionDescriptor descriptor, ByteBuf buf) {
        this.name = name;
        this.descriptor = descriptor;

        ownerName = NetworkTools.readString(buf);
        if (buf.readBoolean()) {
            owner = new UUID(buf.readLong(), buf.readLong());
        } else {
            owner = null;
        }

        terrainType = NetworkTools.readEnum(buf, TerrainType.values());
        NetworkTools.readEnumCollection(buf, featureTypes, FeatureType.values());
        NetworkTools.readEnumCollection(buf, structureTypes, StructureType.values());
        NetworkTools.readEnumCollection(buf, effectTypes, EffectType.values());

        biomes.clear();
        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            int id = buf.readInt();
            Biome biome = Biome.getBiome(id);
            if (biome != null) {
                biomes.add(biome);
            } else {
                biomes.add(Biomes.PLAINS);
            }
        }
        controllerType = NetworkTools.readEnum(buf, ControllerType.values());
        digitString = NetworkTools.readString(buf);

        forcedDimensionSeed = buf.readLong();
        baseSeed = buf.readLong();
        worldVersion = buf.readInt();

        Block block = Block.REGISTRY.getObjectById(buf.readInt());
        int meta = buf.readInt();
        baseBlockForTerrain = block.getStateFromMeta(meta);
        block = Block.REGISTRY.getObjectById(buf.readInt());
        meta = buf.readInt();
        tendrilBlock = block.getStateFromMeta(meta);

        pyramidBlocks = readBlockArrayFromBuf(buf);
        sphereBlocks = readBlockArrayFromBuf(buf);
        hugeSphereBlocks = readBlockArrayFromBuf(buf);
        scatteredSphereBlocks = readBlockArrayFromBuf(buf);
        liquidSphereBlocks = readBlockArrayFromBuf(buf);
        liquidSphereFluids = readFluidArrayFromBuf(buf);
        hugeLiquidSphereBlocks = readBlockArrayFromBuf(buf);
        hugeLiquidSphereFluids = readFluidArrayFromBuf(buf);

        block = Block.REGISTRY.getObjectById(buf.readInt());
        meta = buf.readInt();
        canyonBlock = block.getStateFromMeta(meta);
        fluidForTerrain = Block.REGISTRY.getObjectById(buf.readInt());

        extraOregen = readBlockArrayFromBuf(buf);

        fluidsForLakes = readFluidArrayFromBuf(buf);

        peaceful = buf.readBoolean();
        noanimals = buf.readBoolean();
        shelter = buf.readBoolean();
        respawnHere = buf.readBoolean();
        cheater = buf.readBoolean();

        celestialAngle = NetworkTools.readFloat(buf);
        timeSpeed = NetworkTools.readFloat(buf);

        probeCounter = buf.readInt();
        actualRfCost = buf.readInt();

        injectedDimlets = NetworkTools.readString(buf);

        skyDescriptor = new SkyDescriptor.Builder().fromBytes(buf).build();
        calculateCelestialBodyDescriptors();

        weatherDescriptor = new WeatherDescriptor.Builder().fromBytes(buf).build();

        patreon1 = buf.readLong();

        extraMobs.clear();
        size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            String className = NetworkTools.readString(buf);
            int chance = buf.readInt();
            int minGroup = buf.readInt();
            int maxGroup = buf.readInt();
            int maxLoaded = buf.readInt();

            try {
                Class<? extends EntityLiving> c = GenericTools.castClass(Class.forName(className), EntityLiving.class);
                MobDescriptor mob = new MobDescriptor(c, chance, minGroup, maxGroup, maxLoaded);
                extraMobs.add(mob);
            } catch (ClassNotFoundException e) {
                Logging.logError("Cannot find class: " + className + "!", e);
            }
        }

        size = buf.readInt();
        dimensionTypes = new String[size];
        for (int i = 0 ; i < size ; i++) {
            dimensionTypes[i] = NetworkTools.readString(buf);
        }

        setupBiomeMapping();
    }

    private static Block[] readFluidArrayFromBuf(ByteBuf buf) {
        List<Block> blocks = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            blocks.add(Block.REGISTRY.getObjectById(buf.readInt()));
        }
        return blocks.toArray(new Block[blocks.size()]);
    }

    private static IBlockState[] readBlockArrayFromBuf(ByteBuf buf) {
        int size = buf.readInt();
        List<IBlockState> blocksMeta = new ArrayList<>();
        for (int i = 0 ; i < size ; i++) {
            Block b = Block.REGISTRY.getObjectById(buf.readInt());
            int m = buf.readInt();
            blocksMeta.add(b.getStateFromMeta(m));
        }
        return blocksMeta.toArray(new IBlockState[blocksMeta.size()]);
    }

    public BlockPos getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(BlockPos spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void setSkyDescriptor(SkyDescriptor sd) {
        skyDescriptor = sd;
        calculateCelestialBodyDescriptors();
    }

    private void calculateCelestialBodyDescriptors() {
        List<CelestialBodyType> celestialBodies = skyDescriptor.getCelestialBodies();
        // Find the most suitable sun and moon. This is typically the largest sun in the list of celestial bodies.
        int sunidx = -1;
        int bestsun = 0;
        int moonidx = -1;
        int bestmoon = 0;
        for (int i = 0 ; i < celestialBodies.size() ; i++) {
            CelestialBodyType type = celestialBodies.get(i);
            if (type.getGoodSunFactor() > bestsun) {
                bestsun = type.getGoodSunFactor();
                sunidx = i;
            }
            if (type.getGoodMoonFactor() > bestmoon) {
                bestmoon = type.getGoodMoonFactor();
                moonidx = i;
            }
        }

        // Always the same random series.
        Random random = new Random(123);
        random.nextFloat();
        celestialBodyDescriptors = new ArrayList<>();
        for (int i = 0 ; i < celestialBodies.size() ; i++) {
            CelestialBodyType type = celestialBodies.get(i);
            celestialBodyDescriptors.add(new CelestialBodyDescriptor(type, i == sunidx || i == moonidx));
        }
    }

    public static List<Pair<DimletKey,List<DimletKey>>> extractType(DimletType type, List<Pair<DimletKey,List<DimletKey>>> dimlets) {
        List<Pair<DimletKey,List<DimletKey>>> result = new ArrayList<>();
        for (Pair<DimletKey, List<DimletKey>> dimlet : dimlets) {
            if (dimlet.getLeft().getType() == type) {
                result.add(dimlet);
            }
        }
        return result;
    }

    public void updateCostFactor(DimletKey key) {
        actualRfCost += calculateCostFactor(key);
    }

    private int calculateCostFactor(DimletKey key) {
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if (settings == null) {
            Logging.logError("Something went wrong for key: " + key);
            return 0;
        }
        return (int) (settings.getMaintainCost() * PowerConfiguration.afterCreationCostFactor);
    }

    private void addToCost(DimletKey key) {
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if(settings == null) return;
        int rfMaintainCost = settings.getMaintainCost();

        if (rfMaintainCost < 0) {
            int nominalCost = descriptor.calculateNominalCost();
            int rfMinimum = Math.max(10, nominalCost * PowerConfiguration.minimumCostPercentage / 100);

            actualRfCost = actualRfCost - (actualRfCost * (-rfMaintainCost) / 100);
            if (actualRfCost < rfMinimum) {
                actualRfCost = rfMinimum;        // Never consume less then this
            }
        } else {
            actualRfCost += rfMaintainCost;
        }
    }

    public static void getMaterialAndFluidModifiers(List<DimletKey> modifiers, List<IBlockState> blocks, List<Block> fluids) {
        if (modifiers != null) {
            for (DimletKey modifier : modifiers) {
                if (modifier.getType() == DimletType.DIMLET_MATERIAL) {
                    IBlockState block = DimletObjectMapping.getBlock(modifier);
                    blocks.add(block);
                } else if (modifier.getType() == DimletType.DIMLET_LIQUID) {
                    Block fluid = DimletObjectMapping.getFluid(modifier);
                    fluids.add(fluid);
                }
            }
        }
    }

    public IBlockState getFeatureBlock(Random random, Map<FeatureType, List<DimletKey>> modifiersForFeature, FeatureType featureType) {
        IBlockState block;
        if (featureTypes.contains(featureType)) {
            List<IBlockState> blocks = new ArrayList<>();
            List<Block> fluids = new ArrayList<>();
            getMaterialAndFluidModifiers(modifiersForFeature.get(featureType), blocks, fluids);

            if (!blocks.isEmpty()) {
                block = blocks.get(random.nextInt(blocks.size()));
                if (block == null) {
                    block = Blocks.STONE.getDefaultState();     // This is the default in case None was specified.
                }
            } else {
                // Nothing was specified. With a relatively big chance we use stone. But there is also a chance that the material will be something else.
                if (random.nextFloat() < WorldgenConfiguration.randomFeatureMaterialChance) {
                    DimletKey key = DimletRandomizer.getRandomMaterialBlock(random);
                    if (key != null) {
                        actualRfCost += calculateCostFactor(key);
                        block = DimletObjectMapping.getBlock(key);
                    } else {
                        block = Blocks.STONE.getDefaultState();
                    }
                } else {
                    block = Blocks.STONE.getDefaultState();
                }

            }
        } else {
            block = Blocks.STONE.getDefaultState();
        }
        return block;
    }

    private void setupBiomeMapping() {
        biomeMapping.clear();
        if (controllerType == ControllerType.CONTROLLER_FILTERED) {
            final Set<Integer> ids = new HashSet<>();
            for (Biome biome : biomes) {
                if (biome != null) {
                    ids.add(Biome.getIdForBiome(biome));
                } else {
                    ids.add(Biome.getIdForBiome(Biomes.PLAINS));
                }
            }

            ControllerType.BiomeFilter biomeFilter = new ControllerType.BiomeFilter() {
                @Override
                public boolean match(Biome biome) {
                    return ids.contains(Biome.getIdForBiome(biome));
                }

                @Override
                public double calculateBiomeDistance(Biome a, Biome b) {
                    return calculateBiomeDistance(a, b, false, false, false);
                }
            };
            BiomeControllerMapping.makeFilteredBiomeMap(biomeMapping, biomeFilter);
        }
    }

    public DimensionDescriptor getDescriptor() {
        return descriptor;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    public void setOwner(String name, UUID o) {
        ownerName = name;
        owner = o;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(TerrainType type) {
        terrainType = type;
    }

    public boolean hasFeatureType(FeatureType type) {
        return featureTypes.contains(type);
    }

    public Set<FeatureType> getFeatureTypes() {
        return featureTypes;
    }

    public boolean hasStructureType(StructureType type) {
        return structureTypes.contains(type);
    }

    public Set<StructureType> getStructureTypes() {
        return structureTypes;
    }

    public boolean hasEffectType(EffectType type) {
        return effectTypes.contains(type);
    }

    public Set<EffectType> getEffectTypes() {
        return effectTypes;
    }

    public List<Biome> getBiomes() {
        return biomes;
    }

    public Map<Integer, Integer> getBiomeMapping() {
        if (biomeMapping.isEmpty()) {
            setupBiomeMapping();
        }
        return biomeMapping;
    }

    public ControllerType getControllerType() {
        return controllerType;
    }

    public void setControllerType(ControllerType controllerType) {
        this.controllerType = controllerType;
    }

    public String getDigitString() {
        return digitString;
    }

    public void setDigitString(String digitString) {
        this.digitString = digitString;
    }

    public IBlockState getBaseBlockForTerrain() {
        return baseBlockForTerrain;
    }

    public void setBaseBlockForTerrain(IBlockState blockMeta) { baseBlockForTerrain = blockMeta; }

    public IBlockState getTendrilBlock() {
        return tendrilBlock;
    }

    public void setTendrilBlock(IBlockState block) {
        tendrilBlock = block;
    }

    public IBlockState getCanyonBlock() {
        return canyonBlock;
    }

    public void setCanyonBlock(IBlockState canyonBlock) {
        this.canyonBlock = canyonBlock;
    }

    public IBlockState[] getPyramidBlocks() {
        return pyramidBlocks;
    }

    public void setPyramidBlocks(IBlockState[] pyramidBlocks) {
        this.pyramidBlocks = pyramidBlocks;
    }

    public IBlockState[] getSphereBlocks() {
        return sphereBlocks;
    }

    public void setSphereBlocks(IBlockState[] sphereBlocks) {
        this.sphereBlocks = sphereBlocks;
    }

    public IBlockState[] getHugeSphereBlocks() {
        return hugeSphereBlocks;
    }

    public void setHugeSphereBlocks(IBlockState[] hugeSphereBlocks) {
        this.hugeSphereBlocks = hugeSphereBlocks;
    }

    public void setScatteredSphereBlocks(IBlockState[] scatteredSphereBlocks) {
        this.scatteredSphereBlocks = scatteredSphereBlocks;
    }

    public IBlockState[] getScatteredSphereBlocks() {
        return scatteredSphereBlocks;
    }

    public IBlockState[] getLiquidSphereBlocks() {
        return liquidSphereBlocks;
    }

    public void setLiquidSphereBlocks(IBlockState[] liquidSphereBlocks) {
        this.liquidSphereBlocks = liquidSphereBlocks;
    }

    public Block[] getLiquidSphereFluids() {
        return liquidSphereFluids;
    }

    public void setLiquidSphereFluids(Block[] liquidSphereFluids) {
        this.liquidSphereFluids = liquidSphereFluids;
    }

    public IBlockState[] getHugeLiquidSphereBlocks() {
        return hugeLiquidSphereBlocks;
    }

    public void setHugeLiquidSphereBlocks(IBlockState[] hugeLiquidSphereBlocks) {
        this.hugeLiquidSphereBlocks = hugeLiquidSphereBlocks;
    }

    public Block[] getHugeLiquidSphereFluids() {
        return hugeLiquidSphereFluids;
    }

    public void setHugeLiquidSphereFluids(Block[] hugeLiquidSphereFluids) {
        this.hugeLiquidSphereFluids = hugeLiquidSphereFluids;
    }

    public IBlockState[] getExtraOregen() {
        return extraOregen;
    }

    public void setExtraOregen(IBlockState[] blocks) {
        extraOregen = blocks;
    }

    public Block getFluidForTerrain() {
        return fluidForTerrain;
    }

    public void setFluidForTerrain(Block block) { fluidForTerrain = block; }

    public Block[] getFluidsForLakes() {
        return fluidsForLakes;
    }

    public void setFluidsForLakes(Block[] blocks) {
        fluidsForLakes = blocks;
    }

    public SkyDescriptor getSkyDescriptor() {
        return skyDescriptor;
    }

    public WeatherDescriptor getWeatherDescriptor() {
        return weatherDescriptor;
    }

    public void setWeatherDescriptor(WeatherDescriptor weatherDescriptor) {
        this.weatherDescriptor = weatherDescriptor;
    }

    public List<CelestialBodyDescriptor> getCelestialBodyDescriptors() {
        return celestialBodyDescriptors;
    }

    public String[] getDimensionTypes() {
        return dimensionTypes;
    }

    public void setDimensionTypes(String[] dimensionTypes) {
        this.dimensionTypes = dimensionTypes;
    }

    public List<MobDescriptor> getExtraMobs() {
        return extraMobs;
    }

    public boolean isPeaceful() {
        return peaceful;
    }

    public void setPeaceful(boolean peaceful) {
        this.peaceful = peaceful;
    }

    public boolean isNoanimals() {
        return noanimals;
    }

    public void setNoanimals(boolean noanimals) {
        this.noanimals = noanimals;
    }

    public long getPatreon1() {
        return patreon1;
    }

    public boolean isPatreonBitSet(PatreonType patreon) {
        return (patreon1 & (1L << patreon.getBit())) != 0;
    }

    public void setPatreon1(long patreon1) {
        this.patreon1 = patreon1;
    }

    public void togglePatreonBit(PatreonType patreon) {
        patreon1 ^= (1L << patreon.getBit());
    }

    public boolean isShelter() {
        return shelter;
    }

    public void setShelter(boolean shelter) {
        this.shelter = shelter;
    }

    public boolean isRespawnHere() {
        return respawnHere;
    }

    public void setRespawnHere(boolean respawnHere) {
        this.respawnHere = respawnHere;
    }

    public boolean isCheater() {
        return cheater;
    }

    public void setCheater(boolean cheater) {
        this.cheater = cheater;
    }

    public Float getCelestialAngle() {
        return celestialAngle;
    }

    public Float getTimeSpeed() {
        return timeSpeed;
    }

    public void setCelestialAngle(Float celestialAngle) {
        this.celestialAngle = celestialAngle;
    }

    public void setTimeSpeed(Float timeSpeed) {
        this.timeSpeed = timeSpeed;
    }

    public void addProbe() {
        probeCounter++;
    }

    public void removeProbe() {
        probeCounter--;
        if (probeCounter < 0) {
            probeCounter = 0;
        }
    }

    public int getProbeCounter() {
        return probeCounter;
    }

    public int getActualRfCost() {
        return actualRfCost;
    }

    public long getForcedDimensionSeed() {
        return forcedDimensionSeed;
    }

    public long getBaseSeed() {
        return baseSeed;
    }

    public int getWorldVersion() {
        return worldVersion;
    }
}
