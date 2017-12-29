package mcjty.rftoolsdim.commands;

import mcjty.lib.container.InventoryHelper;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CmdCreateDimlet extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<type> <name>";
    }

    @Override
    public String getCommand() {
        return "createdimlet";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "This command only works as a player!"));
            return;
        }
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length != 3) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Bad parameters!"), false);
            return;
        }

        String typeString = fetchString(player, args, 1, "material");
        String name = fetchString(player, args, 2, "");
        DimletType type = DimletType.getTypeByName(typeString);
        if (type == null) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Bad type!"), false);
            return;
        }

        ItemStack dimlet = KnownDimletConfiguration.getDimletStack(new DimletKey(type, name));
        InventoryHelper.mergeItemStack(player.inventory, false, dimlet, 0, 35, null);
    }
}
