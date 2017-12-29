package mcjty.rftoolsdim.items;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.network.PacketGetDimensionEnergy;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealizedDimensionTab extends GenericRFToolsItem {
    private static long lastTime = 0;

    public RealizedDimensionTab() {
        super("realized_dimension_tab");
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        // do nothing - this item doesn't belong in the creative inventory
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if ((!world.isRemote) && player.isSneaking()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            Logging.message(player, tagCompound.getString("descriptionString"));
            int id = tagCompound.getInteger("id");
            if (id != 0) {
                RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
                DimensionInformation information = dimensionManager.getDimensionInformation(id);
                if (information != null) {
                    information.dump(player);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World world, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, world, list, whatIsThis);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            String name = tagCompound.getString("name");
            int id = 0;
            if (name != null) {
                id = tagCompound.getInteger("id");
                if (id == 0) {
                    list.add(TextFormatting.BLUE + "Name: " + name);
                } else {
                    list.add(TextFormatting.BLUE + "Name: " + name + " (Id " + id + ")");
                }
            }

            String descriptionString = tagCompound.getString("descriptionString");
            constructDescriptionHelp(list, descriptionString);

            Integer ticksLeft = tagCompound.getInteger("ticksLeft");
            if (ticksLeft == 0) {
                DimensionInformation information = RfToolsDimensionManager.getDimensionManager(world).getDimensionInformation(id);
                if (information == null) {
                    list.add(TextFormatting.RED + "Dimension information Missing!");
                } else {
                    list.add(TextFormatting.BLUE + "Dimension ready!");
                    int maintainCost = tagCompound.getInteger("rfMaintainCost");
                    int actualCost = information.getActualRfCost();
                    if (actualCost == maintainCost || actualCost == 0) {
                        list.add(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick");
                    } else {
                        list.add(TextFormatting.YELLOW + "    Maintenance cost: " + actualCost + " RF/tick (Specified: " + maintainCost + " RF/tick)");
                    }
                    if (id != 0) {
                        if (System.currentTimeMillis() - lastTime > 500) {
                            lastTime = System.currentTimeMillis();
                            RFToolsDimMessages.INSTANCE.sendToServer(new PacketGetDimensionEnergy(id));
                        }

                        DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
                        int power = storage.getEnergyLevel(id);
                        list.add(TextFormatting.YELLOW + "    Current power: " + power + " RF");
                    }
                }
            } else {
                int createCost = tagCompound.getInteger("rfCreateCost");
                int maintainCost = tagCompound.getInteger("rfMaintainCost");
                int tickCost = tagCompound.getInteger("tickCost");
                int percentage = (tickCost - ticksLeft) * 100 / tickCost;
                list.add(TextFormatting.BLUE + "Dimension progress: " + percentage + "%");
                list.add(TextFormatting.YELLOW + "    Creation cost: " + createCost + " RF/tick");
                list.add(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick");
                list.add(TextFormatting.YELLOW + "    Tick cost: " + tickCost + " ticks");
            }
        }
    }

    private void constructDescriptionHelp(List<String> list, String descriptionString) {
        Map<DimletType,List<DimletKey>> dimletTypeListMap = new HashMap<>();
        for (DimletKey descriptor : DimensionDescriptor.parseDescriptionString(descriptionString)) {
            DimletType type = descriptor.getType();
            if (!dimletTypeListMap.containsKey(type)) {
                dimletTypeListMap.put(type, new ArrayList<>());
            }
            dimletTypeListMap.get(descriptor.getType()).add(descriptor);
        }

        for (Map.Entry<DimletType, List<DimletKey>> entry : dimletTypeListMap.entrySet()) {
            DimletType type = entry.getKey();
            List<DimletKey> keys = entry.getValue();
            if (keys != null && !keys.isEmpty()) {
                if (type == DimletType.DIMLET_DIGIT) {
                    String digitString = "";
                    for (DimletKey key : keys) {
                        digitString += DimletObjectMapping.getDigit(key);
                    }
                    list.add(TextFormatting.GREEN + "Digits " + digitString);
                } else {
                    if (keys.size() == 1) {
                        list.add(TextFormatting.GREEN + type.dimletType.getName() + " 1 dimlet");
                    } else {
                        list.add(TextFormatting.GREEN + type.dimletType.getName() + " " + keys.size() + " dimlets");
                    }
                }
            }
        }
    }
}
