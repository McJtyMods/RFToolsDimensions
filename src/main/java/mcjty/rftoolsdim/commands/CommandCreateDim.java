package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

public class CommandCreateDim implements Command<CommandSourceStack> {

    private static final CommandCreateDim CMD = new CommandCreateDim();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("create")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.word())
                        .then(Commands.argument("descriptor", StringArgumentType.string())
                                .then(Commands.argument("seed", LongArgumentType.longArg())
                                        .executes(CMD))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        String name = context.getArgument("name", String.class);
        String descriptor = context.getArgument("descriptor", String.class);
        long seed = context.getArgument("seed", Long.class);
        String error = DimensionManager.get().createDimension(context.getSource().getLevel(), name, seed,
                descriptor, context.getSource().getPlayerOrException().getUUID());
        if (error != null) {
            context.getSource().sendSuccess(new TextComponent(ChatFormatting.RED + error), true);
        }
        return 0;
    }
}
