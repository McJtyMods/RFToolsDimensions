package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.TeleportationTools;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;

public class CommandTpDim implements Command<CommandSource> {

    private static final CommandTpDim CMD = new CommandTpDim();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("tp")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String name = context.getArgument("name", String.class);
        ServerPlayerEntity player = context.getSource().asPlayer();
        int x = player.getPosition().getX();
        int z = player.getPosition().getZ();
        ResourceLocation id = new ResourceLocation(name);
        DimensionType type = DimensionType.byName(id);
        if (type == null) {
            context.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Can't find dimension!"), true);
            return 0;
        }
        TeleportationTools.teleport(player, type, x, 200, z, Direction.NORTH);
        return 0;
    }
}
