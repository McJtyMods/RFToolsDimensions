package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandListDim implements Command<CommandSource> {

    private static final CommandListDim CMD = new CommandListDim();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("list")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        for (DimensionType type : DimensionType.getAll()) {
            context.getSource().sendFeedback(new StringTextComponent(type.toString()), true);
        }
        return 0;
    }
}
