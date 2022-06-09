package mcjty.rftoolsdim.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class CommandDump implements Command<CommandSourceStack> {

    private static final CommandDump CMD = new CommandDump();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("dump")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel world = context.getSource().getLevel();
        ResourceLocation location = world.dimension().location();
        feedback(context, ChatFormatting.BLUE + "Dimension: " + ChatFormatting.WHITE + location.toString());
        DimensionData data = PersistantDimensionManager.get(world).getData(location);
        if (data == null) {
            feedback(context, ChatFormatting.RED + "Not an RFTools Dimensions!");
            return 0;
        }
        feedback(context, ChatFormatting.YELLOW + "Owner: " + ChatFormatting.WHITE + data.getOwner());
        feedback(context, ChatFormatting.BLUE + "Energy: " + ChatFormatting.WHITE + data.getEnergy());

        ChunkGenerator generator = world.getChunkSource().getGenerator();
        if (generator instanceof RFToolsChunkGenerator) {
            DimensionSettings settings = ((RFToolsChunkGenerator) generator).getDimensionSettings();
            feedback(context, ChatFormatting.BLUE + "Seed: " + ChatFormatting.WHITE + settings.getSeed());
        }

        DimensionDescriptor descriptor = data.getDescriptor();
        feedback(context, ChatFormatting.GREEN + "Standard dimlets:");
        for (DimletKey dimlet : descriptor.getDimlets()) {
            feedback(context, ChatFormatting.BLUE + "    " + dimlet.type().name() + ": " + ChatFormatting.WHITE + dimlet.key());
        }

        DimensionDescriptor randomizedDescriptor = data.getRandomizedDescriptor();
        feedback(context, ChatFormatting.GREEN + "Randomized dimlets:");
        for (DimletKey dimlet : randomizedDescriptor.getDimlets()) {
            feedback(context, ChatFormatting.BLUE + "    " + dimlet.type().name() + ": " + ChatFormatting.WHITE + dimlet.key());
        }

        return 0;
    }

    private void feedback(CommandContext<CommandSourceStack> context, String message) {
        context.getSource().sendSuccess(ComponentFactory.literal(message), false);
    }
}
