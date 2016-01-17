package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class KnownDimletConfiguration {

    public static DimletEntry getEntry(DimletKey key) {
        // @todo
        return new DimletEntry(key, 10, 1, 10, 1, false, false);
    }

    public static DimletKey getDimletKey(ItemStack dimletStack) {
        DimletType type = DimletType.values()[dimletStack.getItemDamage()];
        NBTTagCompound tagCompound = dimletStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("dkey")) {
            return new DimletKey(type, tagCompound.getString("dkey"));
        } else {
            return new DimletKey(type, "?");
        }
    }

    public static boolean isBlacklisted(DimletKey key) {
        // @todo
        return false;
    }

    public static boolean isCraftable(DimletKey key) {
        // @todo
        return false;
    }

    public static boolean isSeedDimlet(DimletEntry entry) {
        if (entry == null) {
            return false;
        }
        // @todo
        return entry.getKey().getType() == DimletType.DIMLET_SPECIAL && "Seed".equals(entry.getKey().getId());
    }

    public static String getDisplayName(DimletKey key) {
        // @todo
        return key.getId();
    }
}
