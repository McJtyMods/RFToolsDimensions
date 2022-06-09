package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandSetPower implements Command<CommandSourceStack> {

    private static final CommandSetPower CMD = new CommandSetPower();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("setpower")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("power", LongArgumentType.longArg())
                                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        long power = context.getArgument("power", Long.class);
        PersistantDimensionManager mgr = PersistantDimensionManager.get(context.getSource().getLevel());
        DimensionData data = mgr.getData(context.getSource().getLevel().dimension().location());
        if (data == null) {
            context.getSource().sendFailure(ComponentFactory.literal("Not an RFTools Dimension!"));
        } else {
            data.setEnergy(LevelTools.getOverworld(context.getSource().getLevel()), power);
            context.getSource().sendFailure(ComponentFactory.literal("Power set to " + power));
        }
        return 0;
    }
}
