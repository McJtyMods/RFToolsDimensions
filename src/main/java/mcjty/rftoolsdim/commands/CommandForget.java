package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

public class CommandForget implements Command<CommandSourceStack> {

    private static final CommandForget CMD = new CommandForget();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("forget")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        String name = context.getArgument("name", String.class);
        PersistantDimensionManager mgr = PersistantDimensionManager.get(context.getSource().getLevel());
        mgr.forget(new ResourceLocation(RFToolsDim.MODID, name));
        context.getSource().sendSuccess(() -> ComponentFactory.literal(ChatFormatting.YELLOW + "Removed '" + name + "'"), false);
        return 0;
    }
}
