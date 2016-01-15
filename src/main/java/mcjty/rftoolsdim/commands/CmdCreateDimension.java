package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class CmdCreateDimension extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<Name> <Terrain>";
    }

    @Override
    public String getCommand() {
        return "createdim";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "The name and terrain parameters are missing!"));
            return;
        } else if (args.length > 3) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many parameters!"));
            return;
        }

        String name = fetchString(sender, args, 1, "");

        String terrainName = fetchString(sender, args, 2, "Void");
        TerrainType terrainType = TerrainType.getTerrainById(terrainName);
        if (terrainType == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown terrain type!"));
            return;
        }

        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be a player to use this command!"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(sender.getEntityWorld());
        List<DimletKey> descriptors = new ArrayList<>();
        DimensionDescriptor descriptor = new DimensionDescriptor(descriptors, 0);
        int dim = dimensionManager.createNewDimension(sender.getEntityWorld(), descriptor, name, player.getDisplayName().toString(), player.getPersistentID());
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Created dimension: " + dim));

        dimensionManager.save(sender.getEntityWorld());
    }
}
