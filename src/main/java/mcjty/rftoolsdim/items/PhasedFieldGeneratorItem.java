package mcjty.rftoolsdim.items;

import cofh.api.energy.IEnergyContainerItem;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionTickEvent;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PhasedFieldGeneratorItem extends GenericRFToolsItem implements IEnergyContainerItem {

    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public PhasedFieldGeneratorItem() {
        super("phased_field_generator");
        setMaxStackSize(1);

        capacity = PowerConfiguration.PHASEDFIELD_MAXENERGY;
        maxReceive = PowerConfiguration.PHASEDFIELD_RECEIVEPERTICK;
        maxExtract = PowerConfiguration.PHASEDFIELD_CONSUMEPERTICK * DimensionTickEvent.MAXTICKS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int i = 0 ; i <= 8 ; i++) {
            ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName() + i, "inventory"));
//            ModelBakery.addVariantName(this, getRegistryName() + i);
        }

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                int energy = 0;
                if (tagCompound != null) {
                    energy = tagCompound.getInteger("Energy");
                }
                int level = (9*energy) / PowerConfiguration.PHASEDFIELD_MAXENERGY;
                if (level < 0) {
                    level = 0;
                } else if (level > 8) {
                    level = 8;
                }
                return new ModelResourceLocation(getRegistryName() + (8 - level), "inventory");
            }
        });
    }




    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            list.add(EnumChatFormatting.BLUE + "Energy: " + tagCompound.getInteger("Energy") + " RF");
        }
        list.add("This RF/charged module gives a temporary");
        list.add("protection while visiting an unpowered dimension.");
        list.add("Use at your own risk and don't let power run out!");
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (container.getTagCompound() == null) {
            container.setTagCompound(new NBTTagCompound());
        }
        int energy = container.getTagCompound().getInteger("Energy");
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.getTagCompound().setInteger("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
            return 0;
        }
        int energy = container.getTagCompound().getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.getTagCompound().setInteger("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
            return 0;
        }
        return container.getTagCompound().getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return capacity;
    }
}
