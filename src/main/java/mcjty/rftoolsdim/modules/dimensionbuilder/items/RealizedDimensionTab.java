package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.varia.Logging;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RealizedDimensionTab extends Item {
    private static long lastTime = 0;

    public RealizedDimensionTab() {
        super(new Item.Properties().maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if ((!world.isRemote) && player.isSneaking()) {
            CompoundNBT tagCompound = stack.getTag();
            Logging.message(player, tagCompound.getString("descriptionString"));
            int id = tagCompound.getInt("id");
            if (id != 0) {
                // @todo 1.16
//                RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
//                DimensionInformation information = dimensionManager.getDimensionInformation(id);
//                if (information != null) {
//                    information.dump(player);
//                }
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.16 tooltip system
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            String name = tagCompound.getString("name");
            int id = 0;
            if (name != null) {
                id = tagCompound.getInt("id");
                if (id == 0) {
                    list.add(new StringTextComponent(TextFormatting.BLUE + "Name: " + name));
                } else {
                    list.add(new StringTextComponent(TextFormatting.BLUE + "Name: " + name + " (Id " + id + ")"));
                }
            }

            String descriptionString = tagCompound.getString("descriptionString");
            constructDescriptionHelp(list, descriptionString);

            Integer ticksLeft = tagCompound.getInt("ticksLeft");
            if (ticksLeft == 0) {
                // @todo 1.16
//                DimensionInformation information = RfToolsDimensionManager.getDimensionManagerClient().getDimensionInformation(id);
//                if (information == null) {
//                    list.add(new StringTextComponent(TextFormatting.RED + "Dimension information Missing!"));
//                } else {
//                    list.add(new StringTextComponent(TextFormatting.BLUE + "Dimension ready!"));
//                    int maintainCost = tagCompound.getInt("rfMaintainCost");
//                    int actualCost = information.getActualRfCost();
//                    if (actualCost == maintainCost || actualCost == 0) {
//                        list.add(new StringTextComponent(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
//                    } else {
//                        list.add(new StringTextComponent(TextFormatting.YELLOW + "    Maintenance cost: " + actualCost + " RF/tick (Specified: " + maintainCost + " RF/tick)"));
//                    }
//                    if (id != 0) {
//                        if (System.currentTimeMillis() - lastTime > 500) {
//                            lastTime = System.currentTimeMillis();
//                            RFToolsDimMessages.INSTANCE.sendToServer(new PacketGetDimensionEnergy(id));
//                        }
//
//                        DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
//                        long power = storage.getEnergyLevel(id);
//                        list.add(new StringTextComponent(TextFormatting.YELLOW + "    Current power: " + power + " RF"));
//                    }
//                }
            } else {
                int createCost = tagCompound.getInt("rfCreateCost");
                int maintainCost = tagCompound.getInt("rfMaintainCost");
                int tickCost = tagCompound.getInt("tickCost");
                int percentage = (tickCost - ticksLeft) * 100 / tickCost;
                list.add(new StringTextComponent(TextFormatting.BLUE + "Dimension progress: " + percentage + "%"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Creation cost: " + createCost + " RF/tick"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Tick cost: " + tickCost + " ticks"));
            }
        }
    }

    private void constructDescriptionHelp(List<ITextComponent> list, String descriptionString) {
        // @todo 1.16
//        Map<DimletType,List<DimletKey>> dimletTypeListMap = new HashMap<>();
//        for (DimletKey descriptor : DimensionDescriptor.parseDescriptionString(descriptionString)) {
//            DimletType type = descriptor.getType();
//            if (!dimletTypeListMap.containsKey(type)) {
//                dimletTypeListMap.put(type, new ArrayList<>());
//            }
//            dimletTypeListMap.get(descriptor.getType()).add(descriptor);
//        }
//
//        for (Map.Entry<DimletType, List<DimletKey>> entry : dimletTypeListMap.entrySet()) {
//            DimletType type = entry.getKey();
//            List<DimletKey> keys = entry.getValue();
//            if (keys != null && !keys.isEmpty()) {
//                if (type == DimletType.DIMLET_DIGIT) {
//                    String digitString = "";
//                    for (DimletKey key : keys) {
//                        digitString += DimletObjectMapping.getDigit(key);
//                    }
//                    list.add(new StringTextComponent(TextFormatting.GREEN + "Digits " + digitString));
//                } else {
//                    if (keys.size() == 1) {
//                        list.add(new StringTextComponent(TextFormatting.GREEN + type.dimletType.getName() + " 1 dimlet"));
//                    } else {
//                        list.add(new StringTextComponent(TextFormatting.GREEN + type.dimletType.getName() + " " + keys.size() + " dimlets"));
//                    }
//                }
//            }
//        }
    }
}
