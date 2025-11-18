package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.lib.varia.TagTools;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

public class CommonTags {

    private Set<TagKey<Block>> commonTags = null;

    private void findCommonTags() {
        if (commonTags == null) {
            commonTags = new HashSet<>();
            commonTags.add(BlockTags.SAND);
            commonTags.add(BlockTags.FENCES);
            commonTags.add(BlockTags.SAPLINGS);
            commonTags.add(BlockTags.LEAVES);
            commonTags.add(BlockTags.LOGS);
            commonTags.add(BlockTags.RAILS);
            commonTags.add(BlockTags.SLABS);
            commonTags.add(BlockTags.WOOL);
            commonTags.add(BlockTags.WOOL_CARPETS);
            commonTags.add(BlockTags.CROPS);
            commonTags.add(BlockTags.PLANKS);
            commonTags.add(BlockTags.STAIRS);
            commonTags.add(BlockTags.DIRT);
            commonTags.add(Tags.Blocks.GLASS_BLOCKS);
            commonTags.add(Tags.Blocks.GLASS_PANES);
            commonTags.add(Tags.Blocks.CHESTS);
            commonTags.add(Tags.Blocks.COBBLESTONES);
            commonTags.add(Tags.Blocks.NETHERRACKS);
            commonTags.add(Tags.Blocks.OBSIDIANS);
            commonTags.add(Tags.Blocks.GRAVELS);
            commonTags.add(Tags.Blocks.SANDSTONE_BLOCKS);
            commonTags.add(Tags.Blocks.END_STONES);
            commonTags.add(Tags.Blocks.STONES);

            commonTags.add(Tags.Blocks.ORES);
            commonTags.add(Tags.Blocks.ORES_COAL);
            commonTags.add(Tags.Blocks.ORES_DIAMOND);
            commonTags.add(Tags.Blocks.ORES_EMERALD);
            commonTags.add(Tags.Blocks.ORES_GOLD);
            commonTags.add(Tags.Blocks.ORES_REDSTONE);
            commonTags.add(Tags.Blocks.ORES_QUARTZ);
            commonTags.add(Tags.Blocks.ORES_IRON);
            commonTags.add(Tags.Blocks.ORES_LAPIS);
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "ores/copper")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "ores/tin")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "ores/silver")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "ores/manganese")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "ores/platinum")));

            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_COAL);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_DIAMOND);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_EMERALD);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_GOLD);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_REDSTONE);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_IRON);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_LAPIS);
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/copper")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/tin")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/silver")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/manganese")));
            commonTags.add(TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/platinum")));
        }
    }

    public boolean isCommon(TagKey<Block> id) {
        findCommonTags();
        return commonTags.contains(id);
    }

    public void clear() {
        commonTags = null;
    }
}
