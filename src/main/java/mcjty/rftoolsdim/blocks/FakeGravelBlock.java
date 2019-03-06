package mcjty.rftoolsdim.blocks;

import mcjty.lib.McJtyRegister;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeGravelBlock extends Block {

    public FakeGravelBlock() {
        super(Material.GROUND);
        setHardness(0.6f);
        setUnlocalizedName(RFToolsDim.MODID + "." + "fake_gravel");
        setRegistryName("fake_gravel");
//        setLightLevel(0.6f);
//        setCreativeTab(RFToolsDim.setup.getTab());
        McJtyRegister.registerLater(this, RFToolsDim.instance, ItemBlock::new);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(Blocks.GRAVEL));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
