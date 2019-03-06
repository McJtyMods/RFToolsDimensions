package mcjty.rftoolsdim.blocks;

import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class GenericRFToolsBlock<T extends GenericTileEntity, C extends Container> extends GenericBlock<T, C> {

    public GenericRFToolsBlock(Material material, Class<? extends T> tileEntityClass, BiFunction<EntityPlayer, IInventory, C> containerFactory, String name, boolean isContainer) {
        super(RFToolsDim.instance, material, tileEntityClass, containerFactory, name, isContainer);
        setCreativeTab(RFToolsDim.setup.getTab());
    }

    public GenericRFToolsBlock(Material material, Class<? extends T> tileEntityClass, BiFunction<EntityPlayer, IInventory, C> containerFactory,
                               Function<Block, ItemBlock> itemBlockFunction, String name, boolean isContainer) {
        super(RFToolsDim.instance, material, tileEntityClass, containerFactory, itemBlockFunction, name, isContainer);
        setCreativeTab(RFToolsDim.setup.getTab());
    }

    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
//        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            if ((!OrphaningCardItem.isPrivileged(player, world)) && (!player.getPersistentID().equals(genericTileEntity.getOwnerUUID()))) {
//                int securityChannel = genericTileEntity.getSecurityChannel();
//                if (securityChannel != -1) {
//                    SecurityChannels securityChannels = SecurityChannels.getChannels(world);
//                    SecurityChannels.SecurityChannel channel = securityChannels.getChannel(securityChannel);
//                    boolean playerListed = channel.getPlayers().contains(player.getDisplayNameString());
//                    if (channel.isWhitelist() != playerListed) {
//                        Logging.message(player, TextFormatting.RED + "You have no permission to use this block!");
//                        return true;
//                    }
//                }
//            }
//        }
        // @todo
        return false;
    }


}
