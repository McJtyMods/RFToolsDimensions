package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandCreateDim implements Command<CommandSource> {

    private static final CommandCreateDim CMD = new CommandCreateDim();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("createdim")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("name", String.class);
        return 0;
    }
}
