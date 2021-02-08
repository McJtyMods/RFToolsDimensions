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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class PhasedFieldGenerator extends Item implements IEnergyItem, ITooltipSettings {

    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"))
            .infoShift(header(), gold(),
                    TooltipBuilder.parameter("power", this::getEnergyString));

    private String getEnergyString(ItemStack stack) {
        return Integer.toString(stack.hasTag() ? stack.getTag().getInt("Energy") : 0);
    }

    public PhasedFieldGenerator() {
        super(new Properties().group(RFToolsDim.setup.getTab()).maxStackSize(1));

        capacity = DimensionBuilderConfig.PHASEDFIELD_MAXENERGY.get();
        maxReceive = DimensionBuilderConfig.PHASEDFIELD_RECEIVEPERTICK.get();
        maxExtract = DimensionBuilderConfig.PHASEDFIELD_CONSUMEPERTICK.get() * PowerHandler.MAXTICKS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.isEmpty() != newStack.isEmpty()) {
            return true;
        }
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemCapabilityProvider(stack, this);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        tooltipBuilder.get().makeTooltip(getRegistryName(), stack, list, flagIn);
    }

    @Override
    public long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate) {
        CompoundNBT tag = container.getOrCreateTag();
        long energy = tag.getLong("Energy");
        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            tag.putLong("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public long extractEnergyL(ItemStack container, long maxExtract, boolean simulate) {
        CompoundNBT tag = container.getOrCreateTag();
        long energy = tag.getLong("Energy");
        long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

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
        return capacity;
    }

    public static boolean checkValidPhasedFieldGenerator(PlayerEntity player, boolean consume, int tickCost) {
        PlayerInventory inventory = player.inventory;
        for (int i = 0 ; i < PlayerInventory.getHotbarSize() ; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
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