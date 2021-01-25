package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.McJtyLib;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.power.ClientPowerManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionMonitorItem extends Item {
    private static long lastTime = 0;

    public DimensionMonitorItem() {
        super(new Properties().group(RFToolsDim.setup.getTab()).maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.16 tooltip system
    }

    public static void initOverrides(DimensionMonitorItem item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation(RFToolsDim.MODID, "power"), (stack, world, livingEntity) -> {
            World w = world;
            if (w == null) {
                if (livingEntity == null) {
                    return 0;
                }
                w = livingEntity.getEntityWorld();
            }
            long power = ClientPowerManager.get().getPower(w.getDimensionKey().getLocation());
            long max = DimensionConfig.MAX_DIMENSION_POWER.get();
            long level = (9 * power) / max;
            if (level < 0) {
                level = 0;
            } else if (level > 8) {
                level = 9;
            }
            return 8-level;
        });
    }


}
