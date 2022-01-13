package mcjty.rftoolsdim.modules.knowledge.data;

import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

public class CommonTags {

    private Set<ResourceLocation> commonTags = null;

    private void findCommonTags() {
        if (commonTags == null) {
            commonTags = new HashSet<>();
            commonTags.add(BlockTags.SAND.getName());
            commonTags.add(BlockTags.FENCES.getName());
            commonTags.add(BlockTags.SAPLINGS.getName());
            commonTags.add(BlockTags.LEAVES.getName());
            commonTags.add(BlockTags.LOGS.getName());
            commonTags.add(BlockTags.RAILS.getName());
            commonTags.add(BlockTags.SLABS.getName());
            commonTags.add(BlockTags.WOOL.getName());
            commonTags.add(BlockTags.CARPETS.getName());
            commonTags.add(BlockTags.CROPS.getName());
            commonTags.add(BlockTags.PLANKS.getName());
            commonTags.add(BlockTags.STAIRS.getName());
            commonTags.add(BlockTags.DIRT.getName());
            commonTags.add(Tags.Blocks.GLASS.getName());
            commonTags.add(Tags.Blocks.GLASS_PANES.getName());
            commonTags.add(Tags.Blocks.CHESTS.getName());
            commonTags.add(Tags.Blocks.COBBLESTONE.getName());
            commonTags.add(Tags.Blocks.NETHERRACK.getName());
            commonTags.add(Tags.Blocks.OBSIDIAN.getName());
            commonTags.add(Tags.Blocks.GRAVEL.getName());
            commonTags.add(Tags.Blocks.SANDSTONE.getName());
            commonTags.add(Tags.Blocks.END_STONES.getName());
            commonTags.add(Tags.Blocks.STONE.getName());

            commonTags.add(Tags.Blocks.ORES.getName());
            commonTags.add(Tags.Blocks.ORES_COAL.getName());
            commonTags.add(Tags.Blocks.ORES_DIAMOND.getName());
            commonTags.add(Tags.Blocks.ORES_EMERALD.getName());
            commonTags.add(Tags.Blocks.ORES_GOLD.getName());
            commonTags.add(Tags.Blocks.ORES_REDSTONE.getName());
            commonTags.add(Tags.Blocks.ORES_QUARTZ.getName());
            commonTags.add(Tags.Blocks.ORES_IRON.getName());
            commonTags.add(Tags.Blocks.ORES_LAPIS.getName());
            commonTags.add(new ResourceLocation("forge", "ores/copper"));
            commonTags.add(new ResourceLocation("forge", "ores/tin"));
            commonTags.add(new ResourceLocation("forge", "ores/silver"));
            commonTags.add(new ResourceLocation("forge", "ores/manganese"));
            commonTags.add(new ResourceLocation("forge", "ores/platinum"));

            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_COAL.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_DIAMOND.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_EMERALD.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_GOLD.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_REDSTONE.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_QUARTZ.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_IRON.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_LAPIS.getName());
            commonTags.add(new ResourceLocation("forge", "storage_blocks/copper"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/tin"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/silver"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/manganese"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/platinum"));
        }
    }

    public boolean isCommon(ResourceLocation id) {
        findCommonTags();
        return commonTags.contains(id);
    }

    public void clear() {
        commonTags = null;
    }
}
