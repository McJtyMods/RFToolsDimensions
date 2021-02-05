package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledFeature;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class RealizedDimensionTab extends Item {

    public RealizedDimensionTab() {
        super(new Item.Properties().maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if ((!world.isRemote) && player.isSneaking()) {
            CompoundNBT tagCompound = stack.getTag();
            Logging.message(player, tagCompound.getString("descriptor"));
            if (tagCompound.contains("dimension")) {
                String dimension = tagCompound.getString("dimension");
                DimensionData data = PersistantDimensionManager.get(world).getData(new ResourceLocation(dimension));
                if (data != null) {
                    player.sendStatusMessage(new StringTextComponent(TextFormatting.BLUE + "Energy: " + TextFormatting.WHITE + data.getEnergy()), false);
                    DimensionDescriptor descriptor = data.getDescriptor();
                    descriptor.dump(player);
                    player.sendStatusMessage(new StringTextComponent("-----------------------------"), false);
                    DimensionDescriptor randomized = data.getRandomizedDescriptor();
                    randomized.dump(player);
                }

                DimensionId id = DimensionId.fromResourceLocation(new ResourceLocation(dimension));
                ServerWorld serverWorld = id.getWorld();
                ChunkGenerator generator = serverWorld.getChunkProvider().generator;
                if (generator instanceof BaseChunkGenerator) {
                    DimensionSettings settings = ((BaseChunkGenerator) generator).getSettings();
                    player.sendStatusMessage(new StringTextComponent(TextFormatting.BLUE + "Seed: " + TextFormatting.WHITE + settings.getSeed()), false);
                }
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.16 tooltip system
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            ResourceLocation dimension = tagCompound.contains("dimension") ? new ResourceLocation(tagCompound.getString("dimension")) : null;
            if (dimension != null) {
                list.add(new StringTextComponent("Name: " + dimension.getPath()).mergeStyle(TextFormatting.BLUE));
            } else if (tagCompound.contains("name")) {
                String name = tagCompound.getString("name");
                list.add(new StringTextComponent("Name: " + name).mergeStyle(TextFormatting.BLUE));
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
                list.add(new StringTextComponent("Dimension ready!").mergeStyle(TextFormatting.BLUE));
                int maintainCost = tagCompound.getInt("rfMaintainCost");
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Maintenance cost: " + maintainCost + " RF/tick"));
                list.add(new StringTextComponent(TextFormatting.YELLOW + "    Current power: " + power + " RF"));
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
