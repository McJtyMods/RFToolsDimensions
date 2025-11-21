package mcjty.rftoolsdim.modules.various;

import mcjty.lib.blocks.RBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.EndermanLootModifier;
import mcjty.rftoolsdim.modules.various.blocks.ActivityProbeBlock;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;

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
        // @todo 1.21 fix this!
//        dataGen.add(
//                Dob.builder()
//                        .glm("enderman_extra", () -> new EndermanLootModifier(new LootItemCondition[]{
//                                LootTableIdCondition.builder(EntityType.WITHER.getDefaultLootTable().location()).build()
//                        }, List.of(ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "regeneration_ring")), 0.5f, 1, 1, 0, 60, 70))
//                        .glm("dragon_trinket", () -> new TrinketLootModifier(new LootItemCondition[]{
//                                LootTableIdCondition.builder(EntityType.ENDER_DRAGON.getDefaultLootTable().location()).build()
//                        }, List.of(ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "power_star")), 1.0f, 1, 1, 0, 90, 100))
//                        .glm("enderman_trinket", () -> new TrinketLootModifier(new LootItemCondition[]{
//                                LootTableIdCondition.builder(EntityType.ENDERMAN.getDefaultLootTable().location()).build()
//                        }, List.of(ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "warp_pearl")), 0.02f, 1, 1, 0, 90, 100))
//        );
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
