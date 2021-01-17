package mcjty.rftoolsdim.modules.dimlets.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DimletItem extends Item implements ITooltipSettings {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(), TooltipBuilder.parameter("key", DimletItem::isReadyDimlet, DimletItem::getDescription));

    private final boolean isReady;

    public DimletItem(boolean isReady) {
        super(Registration.createStandardProperties());
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
        // @todo
        return "";
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
}
