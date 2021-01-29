package mcjty.rftoolsdim.modules.knowledge.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipExtras;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeKey;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
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
import java.util.List;
import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;

public class LostKnowledgeItem extends Item implements ITooltipSettings, ITooltipExtras {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("pattern", this::getPatternString),
                    parameter("reason", s -> getReasonString(s) != null, this::getReasonString));

    private String getReasonString(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("pattern")) {
            String pattern = tag.getString("pattern");
            KnowledgeKey kkey = new KnowledgeKey(pattern);
            return null;//@todo 1.16 find reason kkey.getReason();
        }

        return null;
    }

    private String getPatternString(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("pattern")) {
            String pattern = tag.getString("pattern");
            KnowledgeKey kkey = new KnowledgeKey(pattern);
            return kkey.getRarity().name().toLowerCase() + " " + kkey.getType().name().toLowerCase();
        }

        return "<Unknown>";
    }

    public LostKnowledgeItem() {
        super(Registration.createStandardProperties());
    }

    @Nullable
    public static KnowledgeKey getKnowledgeKey(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("pattern")) {
            String pattern = tag.getString("pattern");
            KnowledgeKey kkey = new KnowledgeKey(pattern);
            return kkey;
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.addInformation(itemStack, world, list, flags);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    @Override
    public List<Pair<ItemStack, Integer>> getItems(ItemStack stack) {
        List<Pair<ItemStack, Integer>> items = new ArrayList<>();
        KnowledgeKey key = getKnowledgeKey(stack);
        if (key != null) {
            DimletPattern pattern = KnowledgeManager.get().getPattern(key);
            if (pattern != null) {
                DimletItem.addPatternItems(pattern, items);
            }
        }
        return items;
    }

    public static ItemStack createRandomLostKnowledge(World world, DimletRarity rarity, Random random) {
        List<KnowledgeKey> patterns = KnowledgeManager.get().getKnownPatterns(world, rarity);
        if (patterns.isEmpty()) {
            return ItemStack.EMPTY;
        }
        LostKnowledgeItem item = KnowledgeModule.COMMON_LOST_KNOWLEDGE.get();
        switch (rarity) {
            case COMMON:
                item = KnowledgeModule.COMMON_LOST_KNOWLEDGE.get();
                break;
            case UNCOMMON:
                item = KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get();
                break;
            case RARE:
                item = KnowledgeModule.RARE_LOST_KNOWLEDGE.get();
                break;
            case LEGENDARY:
                item = KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get();
                break;
        }
        ItemStack result = new ItemStack(item);
        result.getOrCreateTag().putString("pattern", patterns.get(random.nextInt(patterns.size())).serialize());
        return result;
    }
}
