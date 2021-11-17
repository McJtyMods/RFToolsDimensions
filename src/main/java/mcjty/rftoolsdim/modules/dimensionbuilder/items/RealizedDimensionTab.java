package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledFeature;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class RealizedDimensionTab extends Item {

    public RealizedDimensionTab() {
        super(new Item.Properties().stacksTo(1));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if ((!world.isClientSide) && player.isShiftKeyDown()) {
            CompoundNBT tagCompound = stack.getTag();
            Logging.message(player, tagCompound.getString("descriptor"));
            if (tagCompound.contains("dimension")) {
                String dimension = tagCompound.getString("dimension");
                DimensionData data = PersistantDimensionManager.get(world).getData(new ResourceLocation(dimension));
                if (data != null) {
                    player.displayClientMessage(new StringTextComponent(TextFormatting.BLUE + "Energy: " + TextFormatting.WHITE + data.getEnergy()), false);
                    DimensionDescriptor descriptor = data.getDescriptor();
                    descriptor.dump(player);
                    player.displayClientMessage(new StringTextComponent("-----------------------------"), false);
                    DimensionDescriptor randomized = data.getRandomizedDescriptor();
                    randomized.dump(player);
                }

                RegistryKey<World> id = LevelTools.getId(dimension);
                ServerWorld serverWorld = ServerLifecycleHooks.getCurrentServer().getLevel(id);
                ChunkGenerator generator = serverWorld.getChunkSource().generator;
                if (generator instanceof BaseChunkGenerator) {
                    DimensionSettings settings = ((BaseChunkGenerator) generator).getDimensionSettings();
                    player.displayClientMessage(new StringTextComponent(TextFormatting.BLUE + "Seed: " + TextFormatting.WHITE + settings.getSeed()), false);
                }
            }
        }
        return ActionResult.success(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> list, @Nonnull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        // @todo 1.16 tooltip system
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            ResourceLocation dimension = tagCompound.contains("dimension") ? new ResourceLocation(tagCompound.getString("dimension")) : null;
            if (dimension != null) {
                list.add(new StringTextComponent("Name: " + dimension.getPath()).withStyle(TextFormatting.BLUE));
            } else if (tagCompound.contains("name")) {
                String name = tagCompound.getString("name");
                list.add(new StringTextComponent("Name: " + name).withStyle(TextFormatting.BLUE));
            }

            if (McJtyLib.proxy.isSneaking()) {
                String descriptionString = tagCompound.getString("descriptor");
                String randomizedString = tagCompound.getString("randomized");
                constructDescriptionHelp(list, descriptionString, randomizedString);
            } else {
                list.add(new StringTextComponent(TextFormatting.GREEN + "    <Press Shift>"));
            }

            int ticksLeft = tagCompound.getInt("ticksLeft");
            if (ticksLeft == 0) {
                long power = ClientDimensionData.get().getPower(dimension);
                long max = ClientDimensionData.get().getMaxPower(dimension);
                list.add(new StringTextComponent("Dimension ready!").withStyle(TextFormatting.BLUE));
                int maintainCost = tagCompound.getInt("rfMaintainCost");
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Current power: " + power + " (" + max +")"));
            } else {
                int createCost = tagCompound.getInt("rfCreateCost");
                int maintainCost = tagCompound.getInt("rfMaintainCost");
                int tickCost = tagCompound.getInt("tickCost");
                int percentage = 0;
                if (tickCost != 0) {
                    percentage = (tickCost - ticksLeft) * 100 / tickCost;
                }
                list.add(new StringTextComponent(TextFormatting.BLUE + "Dimension progress: " + percentage + "%"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Creation cost: " + createCost + " RF/tick"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Tick cost: " + tickCost + " ticks"));
            }
        }
    }

    private void constructDescriptionHelp(List<ITextComponent> list, String descriptionString, String randomizedString) {
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
                list.add(new StringTextComponent(TextFormatting.GREEN + "    Terrain: " + TextFormatting.WHITE + compiledDescriptor.getTerrainType().getName()));
            }
            if (compiledDescriptor.getBiomeControllerType() != null) {
                list.add(new StringTextComponent(TextFormatting.GREEN + "    Biome Controller: " + TextFormatting.WHITE + compiledDescriptor.getBiomeControllerType().getName()));
            }
            if (compiledDescriptor.getTimeType() != null) {
                list.add(new StringTextComponent(TextFormatting.GREEN + "    Time: " + TextFormatting.WHITE + compiledDescriptor.getTimeType().getName()));
            }
            for (CompiledFeature feature : compiledDescriptor.getFeatures()) {
                list.add(new StringTextComponent(TextFormatting.GREEN + "    Feature: " + TextFormatting.WHITE + feature.getFeatureType().getName()));
            }
        } else {
            list.add(new StringTextComponent(TextFormatting.RED + "Parse error: " + error.getMessage()));
        }
    }
}
