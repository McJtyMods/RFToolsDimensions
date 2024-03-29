package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CommandForget implements Command<CommandSource> {

    private static final CommandForget CMD = new CommandForget();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("forget")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        String name = context.getArgument("name", String.class);
        PersistantDimensionManager mgr = PersistantDimensionManager.get(context.getSource().getLevel());
        mgr.forget(new ResourceLocation(RFToolsDim.MODID, name));
        context.getSource().sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "Removed '" + name + "'"), false);
        return 0;
    }
}
