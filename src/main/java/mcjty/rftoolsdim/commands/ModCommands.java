package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

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
        // @todo 1.21 reset chunks command
//        ResetChunksCommand.register(dispatcher);
    }

}
