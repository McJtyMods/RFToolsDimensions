package mcjty.rftoolsdim.commands;

public class CommandRftDb extends DefaultCommand {

    public CommandRftDb() {
        super();
        registerCommand(new CmdTestDimlet());
        registerCommand(new CmdCreateDimlet());
        registerCommand(new CmdListDimlets());
        registerCommand(new CmdListBlocks());
        registerCommand(new CmdListLiquids());
        registerCommand(new CmdListMobs());
    }

    @Override
    public String getName() {
        return "rftdb";
    }
}
