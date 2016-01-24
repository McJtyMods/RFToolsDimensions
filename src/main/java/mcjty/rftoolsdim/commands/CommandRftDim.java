package mcjty.rftoolsdim.commands;

public class CommandRftDim extends DefaultCommand {

    public CommandRftDim() {
        super();
        registerCommand(new CmdCreateDimension());
        registerCommand(new CmdListDimensions());
        registerCommand(new CmdTestDimlet());
        registerCommand(new CmdListDimlets());
        registerCommand(new CmdListBlocks());
//        registerCommand(new CmdDelDimension());
//        registerCommand(new CmdTeleport());
//        registerCommand(new CmdDumpRarity());
//        registerCommand(new CmdDumpMRarity());
//        registerCommand(new CmdListEffects());
//        registerCommand(new CmdAddEffect());
//        registerCommand(new CmdDelEffect());
        registerCommand(new CmdSetPower());
        registerCommand(new CmdInfo());
//        registerCommand(new CmdReclaim());
//        registerCommand(new CmdSafeDelete());
//        registerCommand(new CmdCreateTab());
//        registerCommand(new CmdRecover());
//        registerCommand(new CmdSaveDims());
//        registerCommand(new CmdSaveDim());
//        registerCommand(new CmdLoadDim());
//        registerCommand(new CmdSetOwner());
    }

    @Override
    public String getCommandName() {
        return "rftdim";
    }
}
