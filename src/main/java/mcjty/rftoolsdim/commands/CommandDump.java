package mcjty.rftoolsdim.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class CommandDump implements Command<CommandSource> {

    private static final CommandDump CMD = new CommandDump();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("dump")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        OreFeatureConfig config = new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, Blocks.GLOWSTONE.getDefaultState(), 30);
        // @todo 1.16.3
//        Dynamic<JsonElement> serialized = config.serialize(JsonOps.INSTANCE);
//        JsonElement value = serialized.getValue();
//        System.out.println("json = " + GSON.toJson(value));
        return 0;
    }
}
