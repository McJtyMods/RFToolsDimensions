package mcjty.rftoolsdim.modules.knowledge.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeKey;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;

public class LostKnowledgeItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("pattern", this::getPatternString),
                    parameter("reason", s -> getReasonString(s) != null, this::getReasonString));

    private final DimletRarity rarity;

    private String getReasonString(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("reason")) {
            return tag.getString("reason");
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

    public LostKnowledgeItem(DimletRarity rarity) {
        super(Registration.createStandardProperties());
        this.rarity = rarity;
    }

    public DimletRarity getRarity() {
        return rarity;
    }

    @Nullable
    public static KnowledgeKey getKnowledgeKey(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("pattern")) {
            String pattern = tag.getString("pattern");
            return new KnowledgeKey(pattern);
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, list, flags);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    public static ItemStack createUnresearchedLostKnowledge(DimletRarity rarity) {
        switch (rarity) {
            case COMMON:
                return new ItemStack(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get());
            case UNCOMMON:
                return new ItemStack(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get());
            case RARE:
                return new ItemStack(KnowledgeModule.RARE_LOST_KNOWLEDGE.get());
            case LEGENDARY:
                return new ItemStack(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get());
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack createLostKnowledge(World world, DimletKey key) {
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        if (settings != null) {
            KnowledgeKey kkey = KnowledgeManager.get().getKnowledgeKey(LevelTools.getOverworld(world).getSeed(), key);
            if (kkey != null) {
                DimletRarity rarity = settings.getRarity();
                return createLostKnowledgeStack(world, rarity, kkey);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack createRandomLostKnowledge(World world, DimletRarity rarity, Random random) {
        List<KnowledgeKey> patterns = KnowledgeManager.get().getKnownPatterns(world, rarity);
        if (patterns.isEmpty()) {
            return ItemStack.EMPTY;
        }
        KnowledgeKey kkey = patterns.get(random.nextInt(patterns.size()));
        return createLostKnowledgeStack(world, rarity, kkey);
    }

    private static ItemStack createLostKnowledgeStack(World world, DimletRarity rarity, KnowledgeKey kkey) {
        LostKnowledgeItem item = getKnowledgeItem(rarity);
        ItemStack result = new ItemStack(item);
        result.getOrCreateTag().putString("pattern", kkey.serialize());
        String reason = KnowledgeManager.get().getReason(world, kkey);
        if (reason != null) {
            result.getTag().putString("reason", reason);
        }
        return result;
    }

    private static LostKnowledgeItem getKnowledgeItem(DimletRarity rarity) {
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
        return item;
    }
}
