package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSamplingSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSliderBuilder;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

public class CommandRefreshChunks implements Command<CommandSourceStack> {

    private static final CommandRefreshChunks CMD = new CommandRefreshChunks();

    public static final String XZSCALE = "xzscale";
    public static final String YSCALE = "yscale";
    public static final String XZFACTOR = "xzfactor";
    public static final String YFACTOR = "yfactor";
    public static final String TOPOFFSET = "topoffset";
    public static final String TOPSIZE = "topsize";
    public static final String TOPTARGET = "toptarget";
    public static final String BOTTOMOFFSET = "bottomoffset";
    public static final String BOTTOMSIZE = "bottomsize";
    public static final String BOTTOMTARGET = "bottomtarget";
    public static final String HORIZONTAL = "horizontal";
    public static final String VERTICAL = "vertical";

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
            NoiseGeneratorSettings settings = chunkGenerator.getNoiseGeneratorSettings();
            if ("list".equals(cmd)) {
                dump(settings);
            } else if (split.length <= 1) {
                double result = switch (cmd) {
// @todo 1.19
//                    case XZSCALE -> settings.noiseSettings().noiseSamplingSettings().xzScale();
//                    case YSCALE -> settings.noiseSettings().noiseSamplingSettings().yScale();
//                    case XZFACTOR -> settings.noiseSettings().noiseSamplingSettings().xzFactor();
//                    case YFACTOR -> settings.noiseSettings().noiseSamplingSettings().yFactor();
//                    case TOPOFFSET -> settings.noiseSettings().topSlideSettings().offset();
//                    case TOPSIZE -> settings.noiseSettings().topSlideSettings().size();
//                    case TOPTARGET -> settings.noiseSettings().topSlideSettings().target();
//                    case BOTTOMOFFSET -> settings.noiseSettings().bottomSlideSettings().offset();
//                    case BOTTOMSIZE -> settings.noiseSettings().bottomSlideSettings().size();
//                    case BOTTOMTARGET -> settings.noiseSettings().bottomSlideSettings().target();
                    case HORIZONTAL -> settings.noiseSettings().noiseSizeHorizontal();
                    case VERTICAL -> settings.noiseSettings().noiseSizeVertical();
                    default -> 0;
                };
                System.out.println(cmd + " = " + result);
            } else {
                double doubleValue = asDouble(split[1]);
                int intValue = asInt(split[1]);
                Consumer<NoiseSettingsBuilder> noiseBuilder = switch (cmd) {
                    case HORIZONTAL -> builder -> builder.noiseSizeHorizontal(intValue);
                    case VERTICAL -> builder -> builder.noiseSizeVertical(intValue);
                    default -> builder -> {};
                };
                Consumer<NoiseSamplingSettingsBuilder> samplingBuilder = switch (cmd) {
                    case XZSCALE -> builder -> builder.xzScale(doubleValue);
                    case YSCALE -> builder -> builder.yScale(doubleValue);
                    case XZFACTOR -> builder -> builder.xzFactor(doubleValue);
                    case YFACTOR -> builder -> builder.yFactor(doubleValue);
                    default -> builder -> {};
                };
                Consumer<NoiseSliderBuilder> topSliderBuilder = switch (cmd) {
                    case TOPOFFSET -> builder -> builder.offset(intValue);
                    case TOPSIZE -> builder -> builder.size(intValue);
                    case TOPTARGET -> builder -> builder.top(doubleValue);
                    default-> builder -> {};
                };
                Consumer<NoiseSliderBuilder> bottomSliderBuilder = switch (cmd) {
                    case BOTTOMOFFSET -> builder -> builder.offset(intValue);
                    case BOTTOMSIZE -> builder -> builder.size(intValue);
                    case BOTTOMTARGET -> builder -> builder.top(doubleValue);
                    default-> builder -> {};
                };
                chunkGenerator.changeSettings(noiseBuilder, samplingBuilder, topSliderBuilder, bottomSliderBuilder);
            }
        }
        return 0;
    }

    private void dump(NoiseGeneratorSettings settings) {
        // @todo 1.19
//        NoiseSettings noiseSettings = settings.noiseSettings();
//        System.out.println("Noise settings:");
//        NoiseSamplingSettings samplingSettings = noiseSettings.noiseSamplingSettings();
//        System.out.println("    SamplingSettings:   xzfactor=" + samplingSettings.xzFactor()
//                + "  yfactor=" + samplingSettings.yFactor()
//                + "  xzscale=" + samplingSettings.xzScale()
//                + "  yscale=" + samplingSettings.yScale());
//        System.out.println("    TopSlider:  toptarget=" + noiseSettings.topSlideSettings().target()
//                + "  topsize=" + noiseSettings.topSlideSettings().size()
//                + "  topoffset=" + noiseSettings.topSlideSettings().offset());
//        System.out.println("    BottomSlider:  bottomtarget=" + noiseSettings.bottomSlideSettings().target()
//                + "  bottomsize=" + noiseSettings.bottomSlideSettings().size()
//                + "  bottomoffset=" + noiseSettings.bottomSlideSettings().offset());
//        System.out.println("    horizontal=" + noiseSettings.noiseSizeHorizontal()
//                + "  vertical=" + noiseSettings.noiseSizeVertical());
    }

    private static double asDouble(String d) {
        try {
            return Double.parseDouble(d);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int asInt(String d) {
        try {
            return Integer.parseInt(d);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
