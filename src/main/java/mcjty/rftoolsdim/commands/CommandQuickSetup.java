package mcjty.rftoolsdim.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.blocks.BaseBlock;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandQuickSetup implements Command<CommandSourceStack> {

    private static final CommandQuickSetup CMD = new CommandQuickSetup();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("quicksetup")
                .requires(cs -> cs.hasPermission(2))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel world = context.getSource().getLevel();
        ServerPlayer player = context.getSource().getPlayerOrException();
        BlockPos pos = player.blockPosition();
        BlockState quartz = Blocks.QUARTZ_BLOCK.defaultBlockState();
        BlockState glass = Blocks.GLASS.defaultBlockState();
        BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                world.setBlock(pos.offset(dx, -1, dz), quartz, Block.UPDATE_ALL);
                for (int y = 0 ; y < 5 ; y++) {
                    if (dx == -5 || dx == 5 || dz == 5 || dz == -5) {
                        if (y == 4) {
                            world.setBlock(pos.offset(dx, y, dz), glowstone, Block.UPDATE_ALL);
                        } else {
                            world.setBlock(pos.offset(dx, y, dz), glass, Block.UPDATE_ALL);
                        }
                    } else {
                        world.setBlock(pos.offset(dx, y, dz), air, Block.UPDATE_ALL);
                    }
                }
            }
        }
        Block matterTransmitter = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftoolsutility", "matter_transmitter"));
        Block matterReceiver = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftoolsutility", "matter_receiver"));
        Block dialingDevice = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftoolsutility", "dialing_device"));
        Block creativeCell = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftoolspower", "dimensionalcell_creative"));
        world.setBlock(pos.offset(-4, -1, -4), matterTransmitter.defaultBlockState(), Block.UPDATE_ALL);
        world.setBlock(pos.offset(-3, -1, -4), creativeCell.defaultBlockState(), Block.UPDATE_ALL);
        world.setBlock(pos.offset(-2, -1, -4), matterReceiver.defaultBlockState(), Block.UPDATE_ALL);
        world.setBlock(pos.offset(-3, -1, -3), dialingDevice.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP), Block.UPDATE_ALL);

        world.setBlock(pos.offset(3, 0, -4), creativeCell.defaultBlockState(), Block.UPDATE_ALL);
        world.setBlock(pos.offset(4, 0, -4), DimensionBuilderModule.DIMENSION_BUILDER.get().defaultBlockState().setValue(BlockStateProperties.FACING, Direction.SOUTH), Block.UPDATE_ALL);
        world.setBlock(pos.offset(3, 0, 4), WorkbenchModule.WORKBENCH.get().defaultBlockState(), Block.UPDATE_ALL);
        world.setBlock(pos.offset(4, 0, 4), EnscriberModule.ENSCRIBER.get().defaultBlockState(), Block.UPDATE_ALL);

        player.addItem(new ItemStack(DimensionBuilderModule.EMPTY_DIMENSION_TAB.get()));
        player.addItem(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "1")));
        player.addItem(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "2")));
        player.addItem(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "3")));
        player.addItem(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "4")));
        player.addItem(DimletTools.getDimletStack(new DimletKey(DimletType.TERRAIN, "flat")));
        player.addItem(DimletTools.getDimletStack(new DimletKey(DimletType.TERRAIN, "normal")));
        Item chargedPorter = ForgeRegistries.ITEMS.getValue(new ResourceLocation("rftoolsutility", "advanced_charged_porter"));
        player.addItem(new ItemStack(chargedPorter));
        player.addItem(new ItemStack(chargedPorter));

        return 0;
    }
}