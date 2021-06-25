package mcjty.rftoolsdim.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

public class CommandDump implements Command<CommandSource> {

    private static final CommandDump CMD = new CommandDump();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("dump")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getLevel();
        ResourceLocation location = world.dimension().location();
        feedback(context, TextFormatting.BLUE + "Dimension: " + TextFormatting.WHITE + location.toString());
        DimensionData data = PersistantDimensionManager.get(world).getData(location);
        if (data == null) {
            feedback(context, TextFormatting.RED + "Not an RFTools Dimensions!");
            return 0;
        }
        feedback(context, TextFormatting.BLUE + "Energy: " + TextFormatting.WHITE + data.getEnergy());

        ChunkGenerator generator = world.getChunkSource().generator;
        if (generator instanceof BaseChunkGenerator) {
            DimensionSettings settings = ((BaseChunkGenerator) generator).getDimensionSettings();
            feedback(context, TextFormatting.BLUE + "Seed: " + TextFormatting.WHITE + settings.getSeed());
        }

        DimensionDescriptor descriptor = data.getDescriptor();
        feedback(context, TextFormatting.GREEN + "Standard dimlets:");
        for (DimletKey dimlet : descriptor.getDimlets()) {
            feedback(context, TextFormatting.BLUE + "    " + dimlet.getType().name() + ": " + TextFormatting.WHITE + dimlet.getKey());
        }

        DimensionDescriptor randomizedDescriptor = data.getRandomizedDescriptor();
        feedback(context, TextFormatting.GREEN + "Randomized dimlets:");
        for (DimletKey dimlet : randomizedDescriptor.getDimlets()) {
            feedback(context, TextFormatting.BLUE + "    " + dimlet.getType().name() + ": " + TextFormatting.WHITE + dimlet.getKey());
        }

        return 0;
    }

    private void feedback(CommandContext<CommandSource> context, String message) {
        context.getSource().sendSuccess(new StringTextComponent(message), false);
    }
}
