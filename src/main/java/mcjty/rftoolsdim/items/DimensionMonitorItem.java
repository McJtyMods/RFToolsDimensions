package mcjty.rftoolsdim.items;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.network.PacketGetDimensionEnergy;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class DimensionMonitorItem extends GenericRFToolsItem {
    private static long lastTime = 0;

    public DimensionMonitorItem() {
        super("dimension_monitor");
        setMaxStackSize(1);
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
                WorldClient world = Minecraft.getMinecraft().theWorld;
                int id = world.provider.getDimensionId();
                DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
                int energyLevel = storage.getEnergyLevel(id);
                int level = (9*energyLevel) / PowerConfiguration.MAX_DIMENSION_POWER;
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int id = player.worldObj.provider.getDimensionId();
            RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(player.worldObj);
            DimensionInformation dimensionInformation = dimensionManager.getDimensionInformation(id);
            if (dimensionInformation == null) {
                Logging.message(player, "Not an RFTools dimension!");
            } else {
                String name = dimensionInformation.getName();
                DimensionStorage storage = DimensionStorage.getDimensionStorage(player.getEntityWorld());
                int power = storage != null ? storage.getEnergyLevel(id) : 0;

                Logging.message(player, EnumChatFormatting.BLUE + "Name: " + name + " (Id " + id + ")" + EnumChatFormatting.YELLOW + "    Power: " + power + " RF");
                if (player.isSneaking()) {
                    Logging.message(player, EnumChatFormatting.RED + "Description: " + dimensionInformation.getDescriptor().getDescriptionString());
                    System.out.println("Description:  = " + dimensionInformation.getDescriptor().getDescriptionString());
                }
            }
            return stack;
        }
        return stack;
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    public IIcon getIconIndex(ItemStack stack) {
//        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
//        int id = player.worldObj.provider.dimensionId;
//        DimensionStorage storage = DimensionStorage.getDimensionStorage(player.worldObj);
//        int energyLevel = storage.getEnergyLevel(id);
//        int level = (9*energyLevel) / DimletConfiguration.MAX_DIMENSION_POWER;
//        if (level < 0) {
//            level = 0;
//        } else if (level > 8) {
//            level = 8;
//        }
//        return powerLevel[8-level];
//    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        int id = player.worldObj.provider.getDimensionId();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(player.worldObj);
        DimensionInformation dimensionInformation = dimensionManager.getDimensionInformation(id);
        if (dimensionInformation == null) {
            list.add("Not an RFTools dimension!");
        } else {
            if (System.currentTimeMillis() - lastTime > 500) {
                lastTime = System.currentTimeMillis();
                RFToolsDimMessages.INSTANCE.sendToServer(new PacketGetDimensionEnergy(id));
            }
            String name = dimensionInformation.getName();
            DimensionStorage storage = DimensionStorage.getDimensionStorage(player.getEntityWorld());
            int power = storage != null ? storage.getEnergyLevel(id) : 0;

            list.add(EnumChatFormatting.BLUE + "Name: " + name + " (Id " + id + ")");
            list.add(EnumChatFormatting.YELLOW + "Power: " + power + " RF");
        }
    }


}