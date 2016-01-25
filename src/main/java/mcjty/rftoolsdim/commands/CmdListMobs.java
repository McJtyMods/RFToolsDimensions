package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import net.minecraft.command.ICommandSender;

public class CmdListMobs extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "listmobs";
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
        try {
            KnownDimletConfiguration.dumpMobs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
