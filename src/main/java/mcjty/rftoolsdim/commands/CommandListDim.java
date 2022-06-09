package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommandListDim implements Command<CommandSourceStack> {

    private static final CommandListDim CMD = new CommandListDim();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("list")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (ServerLevel world : server.getAllLevels()) {
            ResourceKey<Level> id = world.dimension();
            String output = id.location().getPath();
            DimensionData data = PersistantDimensionManager.get(world).getData(id.location());
            if (data != null) {
                output += " (" + data.getEnergy() + ")";
            }
            context.getSource().sendSuccess(ComponentFactory.literal(output), true);
        }
        return 0;
    }
}
