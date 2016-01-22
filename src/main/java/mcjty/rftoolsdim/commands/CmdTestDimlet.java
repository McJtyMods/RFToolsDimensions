package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.config.DimletRules;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CmdTestDimlet extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<type> <mod> <name>";
    }

    @Override
    public String getCommand() {
        return "testdimlet";
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
        if (args.length != 4) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Bad parameters!"));
            return;
        }

        String typeString = fetchString(sender, args, 1, "material");
        String mod = fetchString(sender, args, 2, "minecraft");
        String name = fetchString(sender, args, 3, "");
        DimletType type = DimletType.getTypeByName(typeString);
        if (type == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Bad type!"));
            return;
        }

        Settings settings = DimletRules.getSettings(type, mod, name);
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + settings.toString()));

    }
}
