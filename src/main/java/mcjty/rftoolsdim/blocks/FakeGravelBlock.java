package mcjty.rftoolsdim.blocks;

import mcjty.lib.McJtyRegister;
import mcjty.lib.compat.CompatBlock;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public class FakeGravelBlock extends CompatBlock {

    public FakeGravelBlock() {
        super(Material.GROUND);
        setHardness(0.6f);
        setUnlocalizedName(RFToolsDim.MODID + "." + "fake_gravel");
        setRegistryName("fake_gravel");
//        setLightLevel(0.6f);
//        setCreativeTab(RFToolsDim.tabRfToolsDim);
        McJtyRegister.registerLater(this, RFToolsDim.instance, ItemBlock.class, null);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return Collections.singletonList(new ItemStack(Blocks.GRAVEL));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
