package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import net.minecraft.command.ICommandSender;

public class CmdListBlocks extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "listblocks";
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
            KnownDimletConfiguration.dumpBlocks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
