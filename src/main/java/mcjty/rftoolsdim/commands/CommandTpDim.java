package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.TeleportationTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.World;

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
        World world = DimensionManager.get().getDimWorld(name);
        if (world == null) {
            RFToolsDim.setup.getLogger().error("Can't find dimension '" + name + "'!");
            return 0;
        }

        DimensionId id = DimensionId.fromWorld(world);
        TeleportationTools.teleport(player, id, x, 200, z, Direction.NORTH);
        return 0;
    }
}
