package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandForgetInvalid implements Command<CommandSource> {

    private static final CommandForgetInvalid CMD = new CommandForgetInvalid();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("forgetinvalid")
                .requires(cs -> cs.hasPermissionLevel(1))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        SharedConstants.developmentMode = true;
        ServerWorld world = context.getSource().getWorld();
        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        Set<Map.Entry<ResourceLocation, DimensionData>> entries = new HashSet<>(mgr.getData().entrySet());
        for (Map.Entry<ResourceLocation, DimensionData> entry : entries) {
            CompiledDescriptor descriptor = DimensionManager.get().getCompiledDescriptor(world, entry.getKey());
            if (descriptor == null) {
                mgr.forget(entry.getKey());
                context.getSource().sendFeedback(new StringTextComponent(TextFormatting.YELLOW + "Removed '" + entry.getKey() + "'"), false);
            }
        }
        return 0;
    }
}
