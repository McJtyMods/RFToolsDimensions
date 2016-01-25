package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.dimlets.DimletDebug;
import net.minecraft.command.ICommandSender;

public class CmdListLiquids extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "listliquids";
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
            DimletDebug.dumpLiquids();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
