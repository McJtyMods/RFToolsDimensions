package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CommandWeather implements Command<CommandSourceStack> {

    private static final CommandWeather CMD = new CommandWeather();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("weather")
                .requires(cs -> cs.hasPermission(1))
                .executes(CMD);
    }


    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        level.setWeatherParameters(600000, 0, false, false);
        level.getServer().getLevel(Level.OVERWORLD).setWeatherParameters(600000, 0, false, false);
        context.getSource().sendSuccess(ComponentFactory.translatable("commands.weather.set.clear"), true);
        return 0;
    }
}
