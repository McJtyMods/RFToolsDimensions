package mcjty.rftoolsdim.commands;

import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.WorldTools;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

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
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "The dimension and player parameters are missing!"));
            return;
        } else if (args.length > 3) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        }

        int dim = fetchInt(sender, args, 1, 0);
        String playerName = fetchString(sender, args, 2, null);

        World world = sender.getEntityWorld();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        if (dimensionManager.getDimensionDescriptor(dim) == null) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!"));
            return;
        }


        for (EntityPlayerMP entityPlayerMP : WorldTools.getPlayerList((WorldServer)world)) {
            if (playerName.equals(entityPlayerMP.getDisplayName())) {
                DimensionInformation information = dimensionManager.getDimensionInformation(dim);
                information.setOwner(playerName, entityPlayerMP.getGameProfile().getId());
                ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.GREEN + "Owner of dimension changed!"));
                dimensionManager.save(world);
                return;
            }
        }
        ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Could not find player!"));
    }
}
