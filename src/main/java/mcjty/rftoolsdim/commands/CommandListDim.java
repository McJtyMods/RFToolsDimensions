package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandListDim implements Command<CommandSource> {

    private static final CommandListDim CMD = new CommandListDim();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("list")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (ServerWorld world : server.getAllLevels()) {
            DimensionId id = DimensionId.fromWorld(world);
            String output = id.getName();
            DimensionData data = PersistantDimensionManager.get(world).getData(id.getRegistryName());
            if (data != null) {
                output += " (" + data.getEnergy() + ")";
            }
            context.getSource().sendSuccess(new StringTextComponent(output), true);
        }
        return 0;
    }
}
