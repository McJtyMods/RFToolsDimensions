package mcjty.rftoolsdim.modules.dimlets.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipExtras;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DimletItem extends Item implements ITooltipSettings, ITooltipExtras {

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
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.addInformation(itemStack, world, list, flags);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            if (this == DimletModule.ADMIN_DIMLET.get()) {
                ItemStack stack = DimletTools.getDimletStack(new DimletKey(DimletType.ADMIN, "owner"));
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
                stack = DimletTools.getDimletStack(new DimletKey(DimletType.ADMIN, "cheater"));
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            } else if (this == DimletModule.DIGIT_DIMLET.get()) {
                for (int i = 0 ; i < 10 ; i++) {
                    items.add(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "" + i)));
                }
            } else {
                items.add(new ItemStack(this));
            }
        }
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
                DimletPattern pattern = KnowledgeManager.get().getPattern(seed, key);
                addPatternItems(pattern, items);
            }

            ItemStack essence = DimletTools.getNeededEssence(key, DimletDictionary.get().getSettings(key));
            if (!essence.isEmpty()) {
                items.add(Pair.of(essence, NOERROR));
            }

            return items;
        }
        return Collections.emptyList();
    }

    public static void addPatternItems(DimletPattern pattern, List<Pair<ItemStack, Integer>> items) {
        if (pattern != null) {
            int cnt = pattern.count(PatternBuilder.SHARD);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(Registration.DIMENSIONAL_SHARD, cnt), NOERROR));
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
