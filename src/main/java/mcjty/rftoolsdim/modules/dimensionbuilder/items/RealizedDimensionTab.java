package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledFeature;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class RealizedDimensionTab extends Item {

    public RealizedDimensionTab() {
        super(new Item.Properties().stacksTo(1));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if ((!world.isClientSide) && player.isShiftKeyDown()) {
            CompoundTag tagCompound = stack.getTag();
            Logging.message(player, tagCompound.getString("descriptor"));
            if (tagCompound.contains("dimension")) {
                String dimension = tagCompound.getString("dimension");
                DimensionData data = PersistantDimensionManager.get(world).getData(new ResourceLocation(dimension));
                if (data != null) {
                    player.displayClientMessage(new TextComponent(ChatFormatting.BLUE + "Energy: " + ChatFormatting.WHITE + data.getEnergy()), false);
                    DimensionDescriptor descriptor = data.getDescriptor();
                    descriptor.dump(player);
                    player.displayClientMessage(new TextComponent("-----------------------------"), false);
                    DimensionDescriptor randomized = data.getRandomizedDescriptor();
                    randomized.dump(player);
                }

                ResourceKey<Level> id = LevelTools.getId(dimension);
                ServerLevel serverWorld = ServerLifecycleHooks.getCurrentServer().getLevel(id);
                ChunkGenerator generator = serverWorld.getChunkSource().getGenerator();
                if (generator instanceof BaseChunkGenerator) {
                    DimensionSettings settings = ((BaseChunkGenerator) generator).getDimensionSettings();
                    player.displayClientMessage(new TextComponent(ChatFormatting.BLUE + "Seed: " + ChatFormatting.WHITE + settings.getSeed()), false);
                }
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        // @todo 1.16 tooltip system
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            ResourceLocation dimension = tagCompound.contains("dimension") ? new ResourceLocation(tagCompound.getString("dimension")) : null;
            if (dimension != null) {
                list.add(new TextComponent("Name: " + dimension.getPath()).withStyle(ChatFormatting.BLUE));
            } else if (tagCompound.contains("name")) {
                String name = tagCompound.getString("name");
                list.add(new TextComponent("Name: " + name).withStyle(ChatFormatting.BLUE));
            }

            if (SafeClientTools.isSneaking()) {
                String descriptionString = tagCompound.getString("descriptor");
                String randomizedString = tagCompound.getString("randomized");
                constructDescriptionHelp(list, descriptionString, randomizedString);
            } else {
                list.add(new TextComponent(ChatFormatting.GREEN + "    <Press Shift>"));
            }

            int ticksLeft = tagCompound.getInt("ticksLeft");
            if (ticksLeft == 0) {
                long power = ClientDimensionData.get().getPower(dimension);
                long max = ClientDimensionData.get().getMaxPower(dimension);
                list.add(new TextComponent("Dimension ready!").withStyle(ChatFormatting.BLUE));
                int maintainCost = tagCompound.getInt("rfMaintainCost");
                list.add(new TextComponent(ChatFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
                list.add(new TextComponent(ChatFormatting.YELLOW + "    Current power: " + power + " (" + max +")"));
            } else {
                int createCost = tagCompound.getInt("rfCreateCost");
                int maintainCost = tagCompound.getInt("rfMaintainCost");
                int tickCost = tagCompound.getInt("tickCost");
                int percentage = 0;
                if (tickCost != 0) {
                    percentage = (tickCost - ticksLeft) * 100 / tickCost;
                }
                list.add(new TextComponent(ChatFormatting.BLUE + "Dimension progress: " + percentage + "%"));
                list.add(new TextComponent(ChatFormatting.YELLOW + "    Creation cost: " + createCost + " RF/tick"));
                list.add(new TextComponent(ChatFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
                list.add(new TextComponent(ChatFormatting.YELLOW + "    Tick cost: " + tickCost + " ticks"));
            }
        }
    }

    private void constructDescriptionHelp(List<Component> list, String descriptionString, String randomizedString) {
        DimensionDescriptor descriptor = new DimensionDescriptor();
        descriptor.read(descriptionString);
        DimensionDescriptor randomizedDescriptor = new DimensionDescriptor();
        if (!randomizedString.isEmpty()) {
            randomizedDescriptor.read(randomizedString);
        }
        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();
        DescriptorError error = compiledDescriptor.compile(descriptor, randomizedDescriptor);
        if (error.isOk()) {
            if (compiledDescriptor.getTerrainType() != null) {
                list.add(new TextComponent(ChatFormatting.GREEN + "    Terrain: " + ChatFormatting.WHITE + compiledDescriptor.getTerrainType().getName()));
            }
            if (compiledDescriptor.getBiomeControllerType() != null) {
                list.add(new TextComponent(ChatFormatting.GREEN + "    Biome Controller: " + ChatFormatting.WHITE + compiledDescriptor.getBiomeControllerType().getName()));
            }
            if (compiledDescriptor.getTimeType() != null) {
                list.add(new TextComponent(ChatFormatting.GREEN + "    Time: " + ChatFormatting.WHITE + compiledDescriptor.getTimeType().getName()));
            }
            for (CompiledFeature feature : compiledDescriptor.getFeatures()) {
                list.add(new TextComponent(ChatFormatting.GREEN + "    Feature: " + ChatFormatting.WHITE + feature.getFeatureType().getName()));
            }
        } else {
            list.add(new TextComponent(ChatFormatting.RED + "Parse error: " + error.getMessage()));
        }
    }
}
