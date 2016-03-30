package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CmdInfo extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "[<dimension number>]";
    }

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        int dim = 0;

        World world = sender.getEntityWorld();
        if (args.length == 2) {
            dim = fetchInt(sender, args, 1, 0);
        } else if (args.length > 2) {
            sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        } else {
            dim = world.provider.getDimension();
        }

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(dim);
        if (information == null) {
            sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!"));
            return;
        }
        sender.addChatMessage(new TextComponentString(TextFormatting.YELLOW + "Dimension ID " + dim));
        sender.addChatMessage(new TextComponentString(TextFormatting.YELLOW + "Description string " + information.getDescriptor().getDescriptionString()));
        String ownerName = information.getOwnerName();
        if (ownerName != null && !ownerName.isEmpty()) {
            sender.addChatMessage(new TextComponentString(TextFormatting.YELLOW + "Owned by: " + ownerName));
        }
        if (sender instanceof EntityPlayer) {
            information.dump((EntityPlayer) sender);
        }
    }
}
