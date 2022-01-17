package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSamplingSettingsBuilder;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

public class CommandRefreshChunks implements Command<CommandSourceStack> {

    private static final CommandRefreshChunks CMD = new CommandRefreshChunks();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("refreshchunks")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("par", StringArgumentType.string())
                    .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        ServerLevel level = context.getSource().getLevel();
        if (level.getChunkSource().getGenerator() instanceof RFToolsChunkGenerator chunkGenerator) {
            String par = context.getArgument("par", String.class);
            String[] split = StringUtils.split(par, '=');
            String cmd = split[0].toLowerCase();
            if (split.length <= 1) {
                NoiseGeneratorSettings settings = chunkGenerator.getNoiseGeneratorSettings();
                double result = switch (cmd) {
                    case "xzscale" -> settings.noiseSettings().noiseSamplingSettings().xzScale();
                    case "yscale" -> settings.noiseSettings().noiseSamplingSettings().yScale();
                    case "xzfactor" -> settings.noiseSettings().noiseSamplingSettings().xzFactor();
                    case "yfactor" -> settings.noiseSettings().noiseSamplingSettings().yFactor();
                    default -> 0;
                };
                System.out.println(cmd + " = " + result);
            } else {
                double value = Double.parseDouble(split[1]);
                Consumer<NoiseSamplingSettingsBuilder> builderConsumer = switch (cmd) {
                    case "xzscale" -> builder -> builder.xzScale(value);
                    case "yscale" -> builder -> builder.yScale(value);
                    case "xzfactor" -> builder -> builder.xzFactor(value);
                    case "yfactor" -> builder -> builder.yFactor(value);
                    default -> builder -> {};
                };
                chunkGenerator.refresh(builderConsumer);
            }
        }
        return 0;
    }
}
