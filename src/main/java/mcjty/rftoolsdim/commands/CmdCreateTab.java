package mcjty.rftoolsdim.commands;

import mcjty.lib.container.InventoryHelper;
import mcjty.rftoolsdim.blocks.enscriber.DimensionEnscriberTileEntity;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CmdCreateTab extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<dimension>";
    }

    @Override
    public String getCommand() {
        return "createtab";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "This command only works as a player!"));
            return;
        }
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 2) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "The dimension parameter is missing!"), false);
            return;
        } else if (args.length > 2) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Too many parameters!"), false);
            return;
        }

        int dim = fetchInt(player, args, 1, 0);
        World world = player.getEntityWorld();

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionDescriptor dimensionDescriptor = dimensionManager.getDimensionDescriptor(dim);
        if (dimensionDescriptor == null) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!"), false);
            return;
        }

        ItemStack tab = DimensionEnscriberTileEntity.createRealizedTab(dimensionDescriptor, player.getEntityWorld());
        InventoryHelper.mergeItemStack(player.inventory, false, tab, 0, 35, null);
    }
}
