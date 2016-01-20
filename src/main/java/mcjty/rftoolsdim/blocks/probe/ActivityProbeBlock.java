package mcjty.rftoolsdim.blocks.probe;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ActivityProbeBlock extends Block {

    public ActivityProbeBlock() {
        super(Material.iron);
        setUnlocalizedName("activity_probe");
        setRegistryName("activity_probe");
        setCreativeTab(RFToolsDim.tabRfToolsDim);
        setHardness(2.0f);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
        GameRegistry.registerBlock(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        if (!world.isRemote) {
            RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
            DimensionInformation information = dimensionManager.getDimensionInformation(world.provider.getDimensionId());
            if (information != null) {
                information.addProbe();
            }
            dimensionManager.save(world);
        }
        return state;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        if (!world.isRemote) {
            RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
            DimensionInformation information = dimensionManager.getDimensionInformation(world.provider.getDimensionId());
            if (information != null) {
                information.removeProbe();
            }
            dimensionManager.save(world);
        }
    }
}
