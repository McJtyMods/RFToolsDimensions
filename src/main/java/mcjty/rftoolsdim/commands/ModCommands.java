package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> commands = dispatcher.register(
                Commands.literal(RFToolsDim.MODID)
                        .then(CommandCreateDim.register(dispatcher))
                        .then(CommandListDim.register(dispatcher))
                        .then(CommandForget.register(dispatcher))
                        .then(CommandForgetInvalid.register(dispatcher))
                        .then(CommandTpDim.register(dispatcher))
                        .then(CommandDump.register(dispatcher))
                        .then(CommandCreateConfig.register(dispatcher))
        );

        dispatcher.register(Commands.literal("dim").redirect(commands));
    }

}
