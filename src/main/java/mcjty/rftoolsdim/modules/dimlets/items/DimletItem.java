package mcjty.rftoolsdim.modules.dimlets.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.items.BaseItem;
import mcjty.lib.tooltips.ITooltipExtras;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.SafeClientTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DimletItem extends BaseItem implements ITooltipSettings, ITooltipExtras {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"), TooltipBuilder.parameter("key", DimletItem::isReadyDimlet, DimletTools::getDimletDescription))
            .infoShift(header(), gold(),
                    TooltipBuilder.parameter("key", DimletItem::isReadyDimlet, DimletTools::getDimletDescription),
                    TooltipBuilder.parameter("rarity", DimletItem::isReadyDimlet, DimletTools::getDimletRarity),
                    TooltipBuilder.parameter("cost", DimletItem::isReadyDimlet, DimletTools::getDimletCost)
            );

    private final DimletType type;
    private final boolean isReady;

    public DimletItem(DimletType type, boolean isReady) {
        super(Registration.createStandardProperties());
        this.type = type;
        this.isReady = isReady;
    }

    public static boolean isReadyDimlet(ItemStack stack) {
        Item item = stack.getItem();
        return isReadyDimlet(item);
    }

    private static boolean isReadyDimlet(Item item) {
        if (item instanceof DimletItem) {
            return ((DimletItem) item).isReady;
        }
        return false;
    }

    /// The empty dimlet crafting ingredient in itself doesn't count as an empty dimlet
    public static boolean isEmptyDimlet(ItemStack stack) {
        if (stack.getItem() instanceof DimletItem) {
            if (stack.getItem() == DimletModule.EMPTY_DIMLET.get()) {
                return false;   // This does not count as empty dimlet
            }
            return !((DimletItem)stack.getItem()).isReady;
        }
        return false;
    }

    public DimletType getType() {
        return type;
    }

    public static DimletType getType(ItemStack stack) {
        if (stack.getItem() instanceof DimletItem) {
            return ((DimletItem) stack.getItem()).getType();
        }
        return null;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flags) {
        super.appendHoverText(itemStack, world, list, flags);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flags);
    }

    @Override
    public List<ItemStack> getItemsForTab() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0 ; i <= 9 ; i++) {
            ItemStack stack = DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, String.valueOf(i)));
            stacks.add(stack);
        }
        stacks.add(DimletTools.getDimletStack(new DimletKey(DimletType.ADMIN, "owner")));
        stacks.add(DimletTools.getDimletStack(new DimletKey(DimletType.ADMIN, "cheater")));
        stacks.add(new ItemStack(DimletModule.EMPTY_TERRAIN_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_ATTRIBUTE_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_FEATURE_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_STRUCTURE_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_BIOME_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_BIOME_CATEGORY_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_BLOCK_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_FLUID_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_TIME_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_TAG_DIMLET.get()));
        stacks.add(new ItemStack(DimletModule.EMPTY_SKY_DIMLET.get()));
        return stacks;
    }

    @Override
    public List<Pair<ItemStack, Integer>> getItems(ItemStack stack) {
        DimletKey key = DimletTools.getDimletKey(stack);
        if (key != null) {
            List<Pair<ItemStack, Integer>> items = new ArrayList<>();
            items.add(Pair.of(DimletTools.getNeededEnergyPart(key), NOAMOUNT));
            items.add(Pair.of(DimletTools.getNeededMemoryPart(key), NOAMOUNT));

            long seed = ClientDimensionData.get().getWorldSeed();
            if (seed != -1) {
                DimletPattern pattern = KnowledgeManager.get().getPattern(SafeClientTools.getClientWorld(), seed, key);
                addPatternItems(pattern, items);
            }

            DimletSettings settings = DimletDictionary.get().getSettings(key);
            if (settings != null) {
                ItemStack essence = DimletTools.getNeededEssence(key, settings);
                if (!essence.isEmpty()) {
                    items.add(Pair.of(essence, NOERROR));
                }
            }

            return items;
        }
        return Collections.emptyList();
    }

    public static void addPatternItems(DimletPattern pattern, List<Pair<ItemStack, Integer>> items) {
        if (pattern != null) {
            int cnt = pattern.count(PatternBuilder.SHARD);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(Registration.DIMENSIONAL_SHARD.get(), cnt), NOERROR));
            }
            cnt = pattern.count(PatternBuilder.LEV0);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(DimletModule.COMMON_ESSENCE.get(), cnt), NOERROR));
            }
            cnt = pattern.count(PatternBuilder.LEV1);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(DimletModule.RARE_ESSENCE.get(), cnt), NOERROR));
            }
            cnt = pattern.count(PatternBuilder.LEV2);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(DimletModule.LEGENDARY_ESSENCE.get(), cnt), NOERROR));
            }
        }
    }
}
