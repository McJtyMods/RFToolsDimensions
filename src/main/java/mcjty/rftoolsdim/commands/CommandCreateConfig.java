package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsdim.modules.dimlets.data.DimletPackages;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.IOException;

public class CommandCreateConfig implements Command<CommandSourceStack> {

    private static final CommandCreateConfig CMD = new CommandCreateConfig();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("config")
                .requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("filename", StringArgumentType.word())
                        .then(Commands.argument("modid", StringArgumentType.string())
                                .executes(CMD)));
    }


    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String filename = context.getArgument("filename", String.class);
        String modid = context.getArgument("modid", String.class);
        try {
            DimletPackages.writePackage(filename, modid);
        } catch (IOException e) {
            context.getSource().sendSuccess(ComponentFactory.literal(ChatFormatting.RED + e.getMessage()), true);
        }
        return 0;
    }
}
