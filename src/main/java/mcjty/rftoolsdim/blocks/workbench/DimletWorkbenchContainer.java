package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class DimletWorkbenchContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_BASE = 2;
    public static final int SLOT_CONTROLLER = 3;
    public static final int SLOT_ENERGY = 4;
    public static final int SLOT_MEMORY = 5;
    public static final int SLOT_TYPE_CONTROLLER = 6;
    public static final int SLOT_ESSENCE = 7;
    public static final int SLOT_BUFFER = 8;

    public static final int SIZE_BUFFER = 6*6;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.knownDimletItem)), CONTAINER_INVENTORY, SLOT_INPUT, 11, 7, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_CRAFTRESULT), CONTAINER_INVENTORY, SLOT_OUTPUT,                                                             11+8+18, 162+6+36+10, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.dimletBaseItem)), CONTAINER_INVENTORY, SLOT_BASE,                      11+8,    162+8, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.dimletControlCircuitItem)), CONTAINER_INVENTORY, SLOT_CONTROLLER,      11+8+18, 162+8, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.dimletEnergyModuleItem)), CONTAINER_INVENTORY, SLOT_ENERGY,            11+8+36, 162+8, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.dimletMemoryUnitItem)), CONTAINER_INVENTORY, SLOT_MEMORY,              11+8,    162+8+18, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.dimletTypeControllerItem)), CONTAINER_INVENTORY, SLOT_TYPE_CONTROLLER, 11+8+18, 162+8+18, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_INPUT/*@@@ ALSO SPECIFIC FOR ESSENCE ITEM!*/), CONTAINER_INVENTORY, SLOT_ESSENCE,                           11+8+36, 162+8+18, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, SLOT_BUFFER, 11, 25, 6, 18, 6, 18);
            layoutPlayerInventorySlots(86, 162);
        }
    };

    public DimletWorkbenchContainer(EntityPlayer player, final IInventory containerInventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, containerInventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        setCrafter(((DimletWorkbenchTileEntity) containerInventory)::craftDimlet);
        generateSlots();
    }

    @Override
    protected boolean mergeItemStacks(ItemStack itemStack, int sourceIndex, SlotDefinition slotDefinition, boolean reverse) {
        if (sourceIndex >= SLOT_BASE && sourceIndex <= SLOT_ESSENCE) {
            return mergeItemStack(itemStack, SLOT_BUFFER, SLOT_BUFFER + SIZE_BUFFER, reverse);
        }
        if (slotDefinition.getType() == SlotType.SLOT_SPECIFICITEM) {
            if (sourceIndex >= SLOT_BUFFER && sourceIndex < SLOT_BUFFER + SIZE_BUFFER) {
                return super.mergeItemStacks(itemStack, sourceIndex, slotDefinition, reverse);
            } else {
                return mergeItemStack(itemStack, SLOT_BUFFER, SLOT_BUFFER + SIZE_BUFFER, reverse);
            }
        }
        return super.mergeItemStacks(itemStack, sourceIndex, slotDefinition, reverse);
    }

    @Override
    public ItemStack slotClick(int index, int button, ClickType mode, EntityPlayer player) {
//        if (index == SLOT_OUTPUT) {
//            if (getSlot(index).getHasStack()) {
//                Achievements.trigger(player, Achievements.dimletMaster);
//            }
//        }
        return super.slotClick(index, button, mode, player);
    }
}
