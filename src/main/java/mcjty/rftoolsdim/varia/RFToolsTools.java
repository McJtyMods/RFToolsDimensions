package mcjty.rftoolsdim.varia;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RFToolsTools {
    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, String soundName, double x, double y, double z, double volume, double pitch) {
        S29PacketSoundEffect soundEffect = new S29PacketSoundEffect(soundName, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.playerEntities.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);
            double d7 = x - entityplayermp.posX;
            double d8 = y - entityplayermp.posY;
            double d9 = z - entityplayermp.posZ;
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                entityplayermp.playerNetServerHandler.sendPacket(soundEffect);
            }
        }
    }

    public static StringBuffer appendIndent(StringBuffer buffer, int indent) {
        return buffer.append(StringUtils.repeat(' ', indent));
    }

    public static void convertNBTtoJson(StringBuffer buffer, NBTTagList tagList, int indent) {
        for (int i = 0 ; i < tagList.tagCount() ; i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);
            appendIndent(buffer, indent).append("{\n");
            convertNBTtoJson(buffer, compound, indent + 4);
            appendIndent(buffer, indent).append("},\n");
        }
    }

    public static void convertNBTtoJson(StringBuffer buffer, NBTTagCompound tagCompound, int indent) {
        boolean first = true;
        for (Object o : tagCompound.getKeySet()) {
            if (!first) {
                buffer.append(",\n");
            }
            first = false;

            String key = (String) o;
            NBTBase tag = tagCompound.getTag(key);
            appendIndent(buffer, indent).append(key).append(':');
            if (tag instanceof NBTTagCompound) {
                NBTTagCompound compound = (NBTTagCompound) tag;
                buffer.append("{\n");
                convertNBTtoJson(buffer, compound, indent + 4);
                appendIndent(buffer, indent).append('}');
            } else if (tag instanceof NBTTagList) {
                NBTTagList list = (NBTTagList) tag;
                buffer.append("[\n");
                convertNBTtoJson(buffer, list, indent + 4);
                appendIndent(buffer, indent).append(']');
            } else {
                buffer.append(tag);
            }
        }
        if (!first) {
            buffer.append("\n");
        }
    }

    public static Map<String, String> modSourceID = null;

    public static String findModID(Object obj) {
        if (modSourceID == null) {
            modSourceID = new HashMap<>();
            for (ModContainer mod : Loader.instance().getModList()) {
                modSourceID.put(mod.getSource().getName(), mod.getModId());
            }

            modSourceID.put("1.8.0.jar", "minecraft");
            modSourceID.put("1.8.8.jar", "minecraft");
            modSourceID.put("1.8.9.jar", "minecraft");
            modSourceID.put("Forge", "minecraft");
        }


        String path;
        if (obj instanceof Class) {
            path = ((Class) obj).getProtectionDomain().getCodeSource().getLocation().toString();
        } else {
            path = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        }
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "<Unknown>";
        }
        String modName = "<Unknown>";
        for (String s : modSourceID.keySet()) {
            if (path.contains(s)) {
                modName = modSourceID.get(s);
                break;
            }
        }

        if (modName.equals("Minecraft Coder Pack")) {
            modName = "minecraft";
        }

        return modName;
    }
}
