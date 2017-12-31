package mcjty.rftoolsdim.commands;

import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class CmdCreateDimension extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<Name> [Terrain|Descriptor] [Seed]";
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
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "You must be a player to use this command!"));
            return;
        }
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 2) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "The name parameter is missing!"), false);
            return;
        } else if (args.length > 4) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Too many parameters!"), false);
            return;
        }

        String name = fetchString(player, args, 1, "");

        String terrainName = fetchString(player, args, 2, "Void");
        List<DimletKey> descriptors;
        if(terrainName.charAt(0) == '@') {
            descriptors = DimensionDescriptor.parseDescriptionString(terrainName);
            for(DimletKey key : descriptors) {
                if(key.getType() == null) {
                    player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Invalid descriptor!"), false);
                    return;
                }
            }
        } else {
            TerrainType terrainType = TerrainType.getTerrainById(terrainName);
            if (terrainType == null) {
                player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "Unknown terrain type!"), false);
                return;
            }
            descriptors = new ArrayList<>(1);
            descriptors.add(new DimletKey(DimletType.DIMLET_TERRAIN, terrainType.getId()));
        }

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(player.getEntityWorld());
        DimensionDescriptor descriptor = new DimensionDescriptor(descriptors, fetchInt(player, args, 3, 0));
        int dim = dimensionManager.createNewDimension(player.getEntityWorld(), descriptor, name, player.getDisplayName().toString(), player.getPersistentID());
        player.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + "Created dimension: " + dim), false);

        dimensionManager.save(player.getEntityWorld());
    }
}
