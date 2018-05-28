package mcjty.rftoolsdim.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public abstract class AbstractRfToolsCommand implements RfToolsCommand {

    protected String fetchString(ICommandSender sender, String[] args, int index, String defaultValue) {
        if(index < 0 || index >= args.length) return defaultValue;
        return args[index];
    }

    protected boolean fetchBool(ICommandSender sender, String[] args, int index, boolean defaultValue) {
        if(index < 0 || index >= args.length) return defaultValue;
        if(args[index].equalsIgnoreCase("true")) {
            return true;
        } else if(!args[index].equalsIgnoreCase("false")) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Parameter is not a valid boolean!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
        return false;
    }

    protected int fetchInt(ICommandSender sender, String[] args, int index, int defaultValue) {
        if(index < 0 || index >= args.length) return defaultValue;
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Parameter is not a valid integer!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return 0;
        }
    }

    protected long fetchLong(ICommandSender sender, String[] args, int index, long defaultValue) {
        if(index < 0 || index >= args.length) return defaultValue;
        try {
            return Long.parseLong(args[index]);
        } catch (NumberFormatException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Parameter is not a valid long integer!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return 0;
        }
    }

    protected float fetchFloat(ICommandSender sender, String[] args, int index, float defaultValue) {
        if(index < 0 || index >= args.length) return defaultValue;
        try {
            return Float.parseFloat(args[index]);
        } catch (NumberFormatException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Parameter is not a valid real number!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return 0.0f;
        }
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
