package mcjty.rftoolsdim.commands;

import mcjty.lib.container.InventoryHelper;
import mcjty.lib.tools.ChatTools;
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
        if (args.length < 2) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "The dimension parameter is missing!"));
            return;
        } else if (args.length > 2) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        }

        int dim = fetchInt(sender, args, 1, 0);
        World world = sender.getEntityWorld();

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionDescriptor dimensionDescriptor = dimensionManager.getDimensionDescriptor(dim);
        if (dimensionDescriptor == null) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!"));
            return;
        }

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            ItemStack tab = DimensionEnscriberTileEntity.createRealizedTab(dimensionDescriptor, sender.getEntityWorld());
            InventoryHelper.mergeItemStack(player.inventory, false, tab, 0, 35, null);
        } else {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "This command only works as a player!"));
        }
    }
}
