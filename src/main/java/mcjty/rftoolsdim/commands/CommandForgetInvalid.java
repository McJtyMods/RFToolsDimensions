package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionCreator;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandForgetInvalid implements Command<CommandSourceStack> {

    private static final CommandForgetInvalid CMD = new CommandForgetInvalid();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("forgetinvalid")
                .requires(cs -> cs.hasPermission(1))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        ServerLevel world = context.getSource().getLevel();
        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        Set<Map.Entry<ResourceLocation, DimensionData>> entries = new HashSet<>(mgr.getData().entrySet());
        for (Map.Entry<ResourceLocation, DimensionData> entry : entries) {
            CompiledDescriptor descriptor = DimensionCreator.get().getCompiledDescriptor(world, entry.getKey());
            if (descriptor == null) {
                mgr.forget(entry.getKey());
                context.getSource().sendSuccess(new TextComponent(ChatFormatting.YELLOW + "Removed '" + entry.getKey() + "'"), false);
            }
        }
        return 0;
    }
}
