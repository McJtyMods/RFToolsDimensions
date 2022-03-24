package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.ResetChunksCommand;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> commands = dispatcher.register(
                Commands.literal(RFToolsDim.MODID)
                        .then(CommandCreateDim.register(dispatcher))
                        .then(CommandListDim.register(dispatcher))
                        .then(CommandRefreshChunks.register(dispatcher))
                        .then(CommandForget.register(dispatcher))
                        .then(CommandForgetInvalid.register(dispatcher))
                        .then(CommandTpDim.register(dispatcher))
                        .then(CommandDump.register(dispatcher))
                        .then(CommandSetPower.register(dispatcher))
                        .then(CommandWeather.register(dispatcher))
                        .then(CommandCreateConfig.register(dispatcher))
                        .then(CommandQuickSetup.register(dispatcher))
        );

        dispatcher.register(Commands.literal("dim").redirect(commands));
        ResetChunksCommand.register(dispatcher);
    }

}
