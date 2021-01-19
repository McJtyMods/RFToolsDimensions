package mcjty.rftoolsdim.modules.dimlets.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipExtras;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DimletItem extends Item implements ITooltipSettings, ITooltipExtras {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(), TooltipBuilder.parameter("key", DimletItem::isReadyDimlet, DimletItem::getDescription));

    private final DimletType type;
    private final boolean isReady;

    public DimletItem(DimletType type, boolean isReady) {
        super(Registration.createStandardProperties());
        this.type = type;
        this.isReady = isReady;
    }

    public static boolean isReadyDimlet(ItemStack stack) {
        if (stack.getItem() instanceof DimletItem) {
            return ((DimletItem)stack.getItem()).isReady;
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

    public static String getDescription(ItemStack stack) {
        DimletKey key = DimletItem.getKey(stack);
        if (key == null) {
            return "<Unknown>";
        } else {
            return DimletClientHelper.getReadableName(key);
        }
    }

    public DimletType getType() {
        return type;
    }

    private static DimletItem getDimletItem(DimletType type) {
        switch (type) {
            case TERRAIN:
                return DimletModule.TERRAIN_DIMLET.get();
            case BIOME_CONTROLLER:
                return DimletModule.BIOME_CONTROLLER_DIMLET.get();
            case BIOME:
                return DimletModule.BIOME_DIMLET.get();
            case FEATURE:
                return DimletModule.FEATURE_DIMLET.get();
            case BLOCK:
                return DimletModule.BLOCK_DIMLET.get();
        }
        return null;
    }

    @Nullable
    public static DimletKey getKey(ItemStack stack) {
        if (stack.getItem() instanceof DimletItem) {
            DimletType type = ((DimletItem) stack.getItem()).getType();
            if (type != null) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    String name = tag.getString("name");
                    return new DimletKey(type, name);
                }
            }
        }
        return null;
    }

    public static ItemStack getDimletStack(DimletKey key) {
        DimletItem item = getDimletItem(key.getType());
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putString("name", key.getKey());
        return stack;
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.addInformation(itemStack, world, list, flags);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    @Override
    public List<Pair<ItemStack, Integer>> getItems(ItemStack stack) {
        DimletKey key = getKey(stack);
        if (key != null) {
            DimletSettings settings = DimletDictionary.get().getSettings(key);
            DimletRarity rarity = settings.getRarity();
            List<Pair<ItemStack, Integer>> items = new ArrayList<>();
            switch (rarity) {
                case COMMON:
                    items.add(Pair.of(new ItemStack(DimletModule.PART_ENERGY_0.get()), NOAMOUNT));
                    items.add(Pair.of(new ItemStack(DimletModule.PART_MEMORY_0.get()), NOAMOUNT));
                    break;
                case UNCOMMON:
                    items.add(Pair.of(new ItemStack(DimletModule.PART_ENERGY_1.get()), NOAMOUNT));
                    items.add(Pair.of(new ItemStack(DimletModule.PART_MEMORY_1.get()), NOAMOUNT));
                    break;
                case RARE:
                    items.add(Pair.of(new ItemStack(DimletModule.PART_ENERGY_2.get()), NOAMOUNT));
                    items.add(Pair.of(new ItemStack(DimletModule.PART_MEMORY_2.get()), NOAMOUNT));
                    break;
                case LEGENDARY:
                    items.add(Pair.of(new ItemStack(DimletModule.PART_ENERGY_3.get()), NOAMOUNT));
                    items.add(Pair.of(new ItemStack(DimletModule.PART_MEMORY_3.get()), NOAMOUNT));
                    break;
            }

            DimletPattern pattern = KnowledgeManager.get().getPattern(DimensionId.overworld().getWorld(), key.getType(), rarity);
            int cnt = pattern.count(PatternBuilder.SHARD);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(Registration.DIMENSIONAL_SHARD, cnt), cnt));
            }
            cnt = pattern.count(PatternBuilder.LEV0);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(DimletModule.COMMON_ESSENCE.get(), cnt), cnt));
            }
            cnt = pattern.count(PatternBuilder.LEV1);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(DimletModule.RARE_ESSENCE.get(), cnt), cnt));
            }
            cnt = pattern.count(PatternBuilder.LEV2);
            if (cnt > 0) {
                items.add(Pair.of(new ItemStack(DimletModule.LEGENDARY_ESSENCE.get(), cnt), cnt));
            }
            // @todo 1.16 add essence item

            return items;
        }
        return Collections.emptyList();
    }
}
