package mcjty.rftoolsdim.commands;

public class CommandRftDbg extends DefaultCommand {

    public CommandRftDbg() {
        super();
        registerCommand(new CmdTestDimlet());
        registerCommand(new CmdListDimlets());
        registerCommand(new CmdListBlocks());
        registerCommand(new CmdListMobs());
    }

    @Override
    public String getCommandName() {
        return "rftdbg";
    }
}
