package mcjty.rftoolsdim.commands;

import mcjty.lib.tools.ChatTools;
import mcjty.rftoolsdim.config.DimletRules;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;

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
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Bad parameters!"));
            return;
        }

        String typeString = fetchString(sender, args, 1, "material");
        String mod = fetchString(sender, args, 2, "minecraft");
        String name = fetchString(sender, args, 3, "");
        DimletType type = DimletType.getTypeByName(typeString);
        if (type == null) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Bad type!"));
            return;
        }

        Settings settings = DimletRules.getSettings(type, mod, name, Collections.emptySet(), 0, Collections.emptyMap());
        ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.GREEN + settings.toString()));
    }
}
