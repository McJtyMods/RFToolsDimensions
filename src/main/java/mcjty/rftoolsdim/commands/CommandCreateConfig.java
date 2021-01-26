package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.modules.dimlets.data.DimletPackages;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class CommandCreateConfig implements Command<CommandSource> {

    private static final CommandCreateConfig CMD = new CommandCreateConfig();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("config")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("filename", StringArgumentType.word())
                        .then(Commands.argument("modid", StringArgumentType.string())
                                .executes(CMD)));
    }


    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String filename = context.getArgument("filename", String.class);
        String modid = context.getArgument("modid", String.class);
        try {
            DimletPackages.writePackage(filename, modid);
        } catch (IOException e) {
            context.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + e.getMessage()), true);
        }
        return 0;
    }
}
