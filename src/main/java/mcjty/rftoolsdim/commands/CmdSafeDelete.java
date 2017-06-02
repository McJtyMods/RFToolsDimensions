package mcjty.rftoolsdim.commands;

import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CmdSafeDelete extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<dimension>";
    }

    @Override
    public String getCommand() {
        return "safedel";
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

        if (!(GeneralConfiguration.playersCanDeleteDimensions || CompatCommandBase.canUseCommand(sender, 3, getCommand()))) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "You have no permission to execute this command!"));
            return;
        }


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
        if (dimensionManager.getDimensionDescriptor(dim) == null) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!"));
            return;
        }

        World w = DimensionManager.getWorld(dim);
        if (w != null) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Dimension is still in use!"));
            return;
        }

        if (!CompatCommandBase.canUseCommand(sender, 3, "safedel")) {
            DimensionInformation information = dimensionManager.getDimensionInformation(dim);
            if (information.getOwner() == null) {
                ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "This dimension has no owner. You cannot delete it!"));
                return;
            }
            if (!(sender instanceof EntityPlayerMP)) {
                ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "This command must be run as a player!"));
                return;
            }
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) sender;
            if (!information.getOwner().equals(entityPlayerMP.getGameProfile().getId())) {
                ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "You are not the owner of this dimension. You cannot delete it!"));
                return;
            }
        }

        RFToolsDim.teleportationManager.removeReceiverDestinations(world, dim);

        dimensionManager.removeDimension(dim);
        dimensionManager.reclaimId(dim);
        dimensionManager.save(world);

        DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);
        dimensionStorage.removeDimension(dim);
        dimensionStorage.save(world);

        if (GeneralConfiguration.dimensionFolderIsDeletedWithSafeDel) {
            File rootDirectory = DimensionManager.getCurrentSaveRootDirectory();
            try {
                FileUtils.deleteDirectory(new File(rootDirectory.getPath() + File.separator + "RFTOOLS" + dim));
                ChatTools.addChatMessage(sender, new TextComponentString("Dimension deleted and dimension folder succesfully wiped!"));
            } catch (IOException e) {
                ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Dimension deleted but dimension folder could not be completely wiped!"));
            }
        } else {
            ChatTools.addChatMessage(sender, new TextComponentString("Dimension deleted. Please remove the dimension folder from disk!"));
        }
    }
}
