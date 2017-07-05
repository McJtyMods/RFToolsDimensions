package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
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
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        } else {
            dim = world.provider.getDimension();
        }

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(dim);
        if (information == null) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }
        ITextComponent component2 = new TextComponentString(TextFormatting.YELLOW + "Dimension ID " + dim);
        if (sender instanceof EntityPlayer) {
            ((EntityPlayer) sender).sendStatusMessage(component2, false);
        } else {
            sender.sendMessage(component2);
        }
        ITextComponent component1 = new TextComponentString(TextFormatting.YELLOW + "Description string " + information.getDescriptor().getDescriptionString());
        if (sender instanceof EntityPlayer) {
            ((EntityPlayer) sender).sendStatusMessage(component1, false);
        } else {
            sender.sendMessage(component1);
        }
        String ownerName = information.getOwnerName();
        if (ownerName != null && !ownerName.isEmpty()) {
            ITextComponent component = new TextComponentString(TextFormatting.YELLOW + "Owned by: " + ownerName);
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
        if (sender instanceof EntityPlayer) {
            information.dump((EntityPlayer) sender);
        }
    }
}
