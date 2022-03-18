package mcjty.rftoolsdim.modules.knowledge.data;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

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
            commonTags.add(BlockTags.CARPETS);
            commonTags.add(BlockTags.CROPS);
            commonTags.add(BlockTags.PLANKS);
            commonTags.add(BlockTags.STAIRS);
            commonTags.add(BlockTags.DIRT);
            commonTags.add(Tags.Blocks.GLASS);
            commonTags.add(Tags.Blocks.GLASS_PANES);
            commonTags.add(Tags.Blocks.CHESTS);
            commonTags.add(Tags.Blocks.COBBLESTONE);
            commonTags.add(Tags.Blocks.NETHERRACK);
            commonTags.add(Tags.Blocks.OBSIDIAN);
            commonTags.add(Tags.Blocks.GRAVEL);
            commonTags.add(Tags.Blocks.SANDSTONE);
            commonTags.add(Tags.Blocks.END_STONES);
            commonTags.add(Tags.Blocks.STONE);

            commonTags.add(Tags.Blocks.ORES);
            commonTags.add(Tags.Blocks.ORES_COAL);
            commonTags.add(Tags.Blocks.ORES_DIAMOND);
            commonTags.add(Tags.Blocks.ORES_EMERALD);
            commonTags.add(Tags.Blocks.ORES_GOLD);
            commonTags.add(Tags.Blocks.ORES_REDSTONE);
            commonTags.add(Tags.Blocks.ORES_QUARTZ);
            commonTags.add(Tags.Blocks.ORES_IRON);
            commonTags.add(Tags.Blocks.ORES_LAPIS);
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores/copper")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores/tin")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores/silver")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores/manganese")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores/platinum")));

            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_COAL);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_DIAMOND);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_EMERALD);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_GOLD);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_REDSTONE);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_QUARTZ);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_IRON);
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_LAPIS);
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "storage_blocks/copper")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "storage_blocks/tin")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "storage_blocks/silver")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "storage_blocks/manganese")));
            commonTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "storage_blocks/platinum")));
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
