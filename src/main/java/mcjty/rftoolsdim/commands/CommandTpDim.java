package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.TeleportationTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.SharedConstants;
import net.minecraft.world.level.Level;

public class CommandTpDim implements Command<CommandSourceStack> {

    private static final CommandTpDim CMD = new CommandTpDim();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("tp")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        String name = context.getArgument("name", String.class);
        ServerPlayer player = context.getSource().getPlayerOrException();
        int x = player.blockPosition().getX();
        int z = player.blockPosition().getZ();
        Level world = DimensionManager.get().getDimWorld(name);
        if (world == null) {
            RFToolsDim.setup.getLogger().error("Can't find dimension '" + name + "'!");
            return 0;
        }

        ResourceKey<Level> id = world.dimension();
        TeleportationTools.teleport(player, id, x, 200, z, Direction.NORTH);
        return 0;
    }
}
