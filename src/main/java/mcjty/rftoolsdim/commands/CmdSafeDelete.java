package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
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

        if (!(GeneralConfiguration.playersCanDeleteDimensions || sender.canUseCommand(3, getCommand()))) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "You have no permission to execute this command!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }


        if (args.length < 2) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "The dimension parameter is missing!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        } else if (args.length > 2) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        int dim = fetchInt(sender, args, 1, 0);
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

        World w = DimensionManager.getWorld(dim);
        if (w != null) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Dimension is still in use!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        if (!sender.canUseCommand(3, "safedel")) {
            DimensionInformation information = dimensionManager.getDimensionInformation(dim);
            if (information.getOwner() == null) {
                ITextComponent component = new TextComponentString(TextFormatting.RED + "This dimension has no owner. You cannot delete it!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
                return;
            }
            if (!(sender instanceof EntityPlayerMP)) {
                ITextComponent component = new TextComponentString(TextFormatting.RED + "This command must be run as a player!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
                return;
            }
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) sender;
            if (!information.getOwner().equals(entityPlayerMP.getGameProfile().getId())) {
                ITextComponent component = new TextComponentString(TextFormatting.RED + "You are not the owner of this dimension. You cannot delete it!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
                return;
            }
        }

        RFToolsDim.teleportationManager.removeReceiverDestinations(world, dim);

        dimensionManager.removeDimension(dim);
        dimensionManager.reclaimId(dim);
        dimensionManager.save(world);

        DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);
        dimensionStorage.removeDimension(dim);
        dimensionStorage.save();

        if (GeneralConfiguration.dimensionFolderIsDeletedWithSafeDel) {
            File rootDirectory = DimensionManager.getCurrentSaveRootDirectory();
            try {
                FileUtils.deleteDirectory(new File(rootDirectory.getPath() + File.separator + "RFTOOLS" + dim));
                ITextComponent component = new TextComponentString("Dimension deleted and dimension folder succesfully wiped!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
            } catch (IOException e) {
                ITextComponent component = new TextComponentString(TextFormatting.RED + "Dimension deleted but dimension folder could not be completely wiped!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
            }
        } else {
            ITextComponent component = new TextComponentString("Dimension deleted. Please remove the dimension folder from disk!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
