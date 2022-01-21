package mcjty.rftoolsdim.modules.dimlets.data;

import net.minecraft.network.FriendlyByteBuf;

public record DimletKey(DimletType type, String key) implements Comparable<DimletKey> {

    public static DimletKey create(FriendlyByteBuf buf) {
        return new DimletKey(DimletType.values()[buf.readInt()], buf.readUtf(32767));
    }

    public static DimletKey create(String serialized) {
        String[] split = serialized.split("#");
        return new DimletKey(DimletType.byName(split[0]), split[1]);
    }

    public String serialize() {
        return type.getShortName() + "#" + key;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeUtf(key);
    }

    @Override
    public int compareTo(DimletKey dimletKey) {
        if (dimletKey.type().equals(type)) {
            return key.compareTo(dimletKey.key);
        } else {
            return type.name().compareTo(dimletKey.type.name());
        }
    }
}
