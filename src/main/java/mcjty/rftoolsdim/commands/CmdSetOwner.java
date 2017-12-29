package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CmdSetOwner extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<id> <owner>";
    }

    @Override
    public String getCommand() {
        return "setowner";
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
        if (args.length < 3) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "The dimension and player parameters are missing!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        } else if (args.length > 3) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        int dim = fetchInt(sender, args, 1, 0);
        String playerName = fetchString(sender, args, 2, null);

        World world = sender.getEntityWorld();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        if (dimensionManager.getDimensionDescriptor(dim) == null) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }


        for (EntityPlayerMP entityPlayerMP : world.getMinecraftServer().getPlayerList().getPlayers()) {
            if (playerName.equals(entityPlayerMP.getName())) {
                DimensionInformation information = dimensionManager.getDimensionInformation(dim);
                information.setOwner(playerName, entityPlayerMP.getGameProfile().getId());
                ITextComponent component = new TextComponentString(TextFormatting.GREEN + "Owner of dimension changed!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
                dimensionManager.save(world);
                return;
            }
        }
        ITextComponent component = new TextComponentString(TextFormatting.RED + "Could not find player!");
        if (sender instanceof EntityPlayer) {
            ((EntityPlayer) sender).sendStatusMessage(component, false);
        } else {
            sender.sendMessage(component);
        }
    }
}
