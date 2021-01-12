package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.TeleportationTools;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

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
        SharedConstants.developmentMode = true;
        String name = context.getArgument("name", String.class);
        ServerPlayerEntity player = context.getSource().asPlayer();
        int x = player.getPosition().getX();
        int z = player.getPosition().getZ();
        ResourceLocation id = new ResourceLocation(name);
        DimensionId type = DimensionId.fromResourceLocation(id);
        if (type == null) {
            if (!name.contains(":")) {
                // Try adding 'rftoolsdim' in front
                id = new ResourceLocation("rftoolsdim:" + name);
                type = DimensionId.fromResourceLocation(id);
            }
            if (type == null) {
                context.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Can't find dimension!"), true);
                return 0;
            }
        }

        TeleportationTools.teleport(player, type, x, 200, z, Direction.NORTH);
        return 0;
    }
}
