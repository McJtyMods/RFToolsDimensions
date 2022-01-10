package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.IEnergyItem;
import mcjty.lib.varia.ItemCapabilityProvider;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.power.PowerHandler;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.world.item.Item.Properties;

public class PhasedFieldGenerator extends Item implements IEnergyItem, ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(),
                    TooltipBuilder.parameter("power", this::getEnergyString));

    private String getEnergyString(ItemStack stack) {
        return Integer.toString(stack.hasTag() ? stack.getTag().getInt("Energy") : 0);
    }

    public PhasedFieldGenerator() {
        super(new Properties().tab(RFToolsDim.setup.getTab()).stacksTo(1));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.isEmpty() != newStack.isEmpty()) {
            return true;
        }
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityProvider(stack, this);
    }


    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.get().makeTooltip(getRegistryName(), stack, list, flagIn);
    }

    @Override
    public long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate) {
        CompoundTag tag = container.getOrCreateTag();
        long energy = tag.getLong("Energy");
        long energyReceived = Math.min(getMaxEnergyStoredL(container) - energy, Math.min(DimensionBuilderConfig.PHASEDFIELD_RECEIVEPERTICK.get(), maxReceive));

        if (!simulate) {
            energy += energyReceived;
            tag.putLong("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public long extractEnergyL(ItemStack container, long maxExtract, boolean simulate) {
        CompoundTag tag = container.getOrCreateTag();
        long energy = tag.getLong("Energy");
        long energyExtracted = Math.min(energy, Math.min(DimensionBuilderConfig.PHASEDFIELD_CONSUMEPERTICK.get() * PowerHandler.MAXTICKS, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            tag.putLong("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public long getEnergyStoredL(ItemStack container) {
        if (container.getTag() == null || !container.getTag().contains("Energy")) {
            return 0;
        }
        return container.getTag().getLong("Energy");
    }

    @Override
    public long getMaxEnergyStoredL(ItemStack container) {
        return DimensionBuilderConfig.PHASEDFIELD_MAXENERGY.get();
    }

    public static boolean checkValidPhasedFieldGenerator(Player player, boolean consume, int tickCost) {
        Inventory inventory = player.getInventory();
        for (int i = 0 ; i < Inventory.getSelectionSize() ; i++) {
            ItemStack slot = inventory.getItem(i);
            if (!slot.isEmpty() && slot.getItem() == DimensionBuilderModule.PHASED_FIELD_GENERATOR.get()) {
                PhasedFieldGenerator pfg = (PhasedFieldGenerator) slot.getItem();
                int energyStored = pfg.getEnergyStored(slot);
                int toConsume;
                if (DimensionConfig.ENABLE_DYNAMIC_PHASECOST.get()) {
                    toConsume = (int) (PowerHandler.MAXTICKS * tickCost * DimensionConfig.DYNAMIC_PHASECOST_AMOUNT.get());
                } else {
                    toConsume = (int) (PowerHandler.MAXTICKS * DimensionBuilderConfig.PHASEDFIELD_CONSUMEPERTICK.get());
                }
                if (energyStored >= toConsume) {
                    if (consume) {
                        pfg.extractEnergy(slot, toConsume, false);
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
