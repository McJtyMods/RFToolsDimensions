package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.IEnergyItem;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.power.PowerHandler;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.data.PhasedFieldGeneratorData;
import mcjty.lib.gui.ManualEntry;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class PhasedFieldGenerator extends Item implements IEnergyItem, ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(),
                    TooltipBuilder.parameter("power", this::getEnergyString)));

    private String getEnergyString(ItemStack stack) {
        return Long.toString(PhasedFieldGeneratorData.getEnergy(stack));
    }

    public PhasedFieldGenerator() {
        super(RFToolsDim.setup.defaultProperties().stacksTo(1));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.isEmpty() != newStack.isEmpty()) {
            return true;
        }
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), stack, list, flagIn);
    }

    @Override
    public long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate) {
        long energy = PhasedFieldGeneratorData.getEnergy(container);
        long energyReceived = Math.min(getMaxEnergyStoredL(container) - energy, Math.min(DimensionBuilderConfig.PHASEDFIELD_RECEIVEPERTICK.get(), maxReceive));

        if (!simulate) {
            energy += energyReceived;
            PhasedFieldGeneratorData.setEnergy(container, energy);
        }
        return energyReceived;
    }

    @Override
    public long extractEnergyL(ItemStack container, long maxExtract, boolean simulate) {
        long energy = PhasedFieldGeneratorData.getEnergy(container);
        long energyExtracted = Math.min(energy, Math.min(DimensionBuilderConfig.PHASEDFIELD_CONSUMEPERTICK.get() * PowerHandler.MAXTICKS, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            PhasedFieldGeneratorData.setEnergy(container, energy);
        }
        return energyExtracted;
    }

    @Override
    public long getEnergyStoredL(ItemStack container) {
        return PhasedFieldGeneratorData.getEnergy(container);
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

    public IEnergyStorage createEnergyStorage(ItemStack container) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int) receiveEnergyL(container, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int) extractEnergyL(container, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return (int) getEnergyStoredL(container);
            }

            @Override
            public int getMaxEnergyStored() {
                return (int) getMaxEnergyStoredL(container);
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    @Override
    public ManualEntry getManualEntry() {
        return ManualHelper.create("rftoolsdim:dimensions/phased_field_generator");
    }

}
