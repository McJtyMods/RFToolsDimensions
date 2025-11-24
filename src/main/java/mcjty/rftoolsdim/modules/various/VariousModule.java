package mcjty.rftoolsdim.modules.various;

import mcjty.lib.blocks.RBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.EndermanLootModifier;
import mcjty.rftoolsdim.modules.various.blocks.ActivityProbeBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.registerSimpleBlock;
import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class VariousModule implements IModule {

    public static final RBlock<ActivityProbeBlock, BlockItem, BlockEntity> ACTIVITY_PROBE = registerSimpleBlock("activity_probe", () -> new ActivityProbeBlock(BlockBuilder.STANDARD_IRON));

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder()
                        .glm("enderman_extra", () -> new EndermanLootModifier(new LootItemCondition[]{
                                LootTableIdCondition.builder(EntityType.ENDERMAN.getDefaultLootTable().location()).build()
                        }, 0.1f, 0.02f, 0.002f, 0f, 0.02f, 0.005f, 0.0001f, 0.00001f))
                        .glm("chest_extra", () -> new EndermanLootModifier(new LootItemCondition[]{
                                LootTableIdCondition.builder(BuiltInLootTables.VILLAGE_CARTOGRAPHER.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.VILLAGE_PLAINS_HOUSE.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.VILLAGE_TOOLSMITH.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.DESERT_PYRAMID.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.RUINED_PORTAL.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.SHIPWRECK_TREASURE.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.JUNGLE_TEMPLE.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.SIMPLE_DUNGEON.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.ABANDONED_MINESHAFT.location()).build()
                        }, 0.05f, 0.01f, 0.001f, 0f, 0.02f, 0.005f, 0.0001f, 0.00001f))
                        .glm("chest_extraplus", () -> new EndermanLootModifier(new LootItemCondition[]{
                                LootTableIdCondition.builder(BuiltInLootTables.STRONGHOLD_LIBRARY.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.BASTION_TREASURE.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.ANCIENT_CITY.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.END_CITY_TREASURE.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.PILLAGER_OUTPOST.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.BURIED_TREASURE.location()).build(),
                                LootTableIdCondition.builder(BuiltInLootTables.WOODLAND_MANSION.location()).build()
                        }, 0.1f, 0.02f, 0.002f, 0.001f, 0.04f, 0.01f, 0.0002f, 0.00002f))
        );
        dataGen.add(
                Dob.blockBuilder(ACTIVITY_PROBE)
                        .ironPickaxeTags()
                        .parentedItem("block/activity_probe")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(ACTIVITY_PROBE.block().get(), BLOCK_FOLDER + "/activity_probe", "block/activity_probe"))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', mcjty.rftoolsbase.modules.various.VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("shard", has(mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())),
                                "sCs", "CFC", "sCs")
        );
    }
}
