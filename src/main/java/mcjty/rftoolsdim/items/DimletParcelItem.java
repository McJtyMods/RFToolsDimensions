package mcjty.rftoolsdim.items;

import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.varia.RFToolsTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class DimletParcelItem extends GenericRFToolsItem {
    public DimletParcelItem() {
        super("dimlet_parcel");
        setMaxStackSize(64);
    }

    private Random random = new Random();

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            int amount = random.nextInt(GeneralConfiguration.maxParcelContents - GeneralConfiguration.minParcelContents + 1) + GeneralConfiguration.minParcelContents;
            if (amount > 0) {
                RFToolsTools.playSound(world, "random.levelup", player.posX, player.posY, player.posZ, 1.0f, 1.0f);
                for (int i = 0 ; i < amount ; i++) {
                    ItemStack part = DimletRandomizer.getRandomPart(random);
                    if (!player.inventory.addItemStackToInventory(part)) {
                        player.entityDropItem(part, 1.05f);
                    }
                }
            }
            player.openContainer.detectAndSendChanges();
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add(EnumChatFormatting.GREEN + "A present for you! Use it well");
        list.add(EnumChatFormatting.GREEN + "Right click to get the gifts");
    }

}
