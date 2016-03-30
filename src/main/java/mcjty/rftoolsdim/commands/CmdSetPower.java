package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.TextComponentString;
import net.minecraft.util.TextFormatting;
import net.minecraft.world.World;

public class CmdSetPower extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "[<rf>]";
    }

    @Override
    public String getCommand() {
        return "setpower";
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
        if (args.length > 2) {
            sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        }

        int rf = fetchInt(sender, args, 1, PowerConfiguration.MAX_DIMENSION_POWER);

        World world = sender.getEntityWorld();
        int dim = world.provider.getDimensionId();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(dim);
        if (information == null) {
            sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Not an RFTools dimension!"));
            return;
        }

        DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
        storage.setEnergyLevel(dim, rf);
        storage.save(world);
    }
}
