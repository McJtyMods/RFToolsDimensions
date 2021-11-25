package mcjty.rftoolsdim.modules.workbench.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeKey;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Set;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class KnowledgeHolderTileEntity extends GenericTileEntity {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(13*8)
            .box(specific(KnowledgeHolderTileEntity::isValidKnowledgeItem).in().out(), 0, 11, 10, 13, 8)
            .playerSlots(11, 158));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Knowledge Holder")
            .containerSupplier(container(WorkbenchModule.CONTAINER_HOLDER, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .setupSync(this));

    public KnowledgeHolderTileEntity() {
        super(WorkbenchModule.TYPE_HOLDER.get());
    }

    private static boolean isValidKnowledgeItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof LostKnowledgeItem;
    }

    public void addKnownKnowledgeKeys(Set<KnowledgeKey> keys) {
        for (int i = 0 ; i < items.getSlots() ; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                KnowledgeKey key = LostKnowledgeItem.getKnowledgeKey(stack);
                keys.add(key);
            }
        }
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(KnowledgeHolderTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/knowledge_holder"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.ROTATION;
            }
        };
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isValidKnowledgeItem(stack);
            }
        };
    }
}
