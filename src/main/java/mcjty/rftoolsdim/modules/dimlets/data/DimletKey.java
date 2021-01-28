package mcjty.rftoolsdim.modules.dimlets.data;

import java.util.Objects;

public class DimletKey implements Comparable<DimletKey> {

    private final DimletType type;
    private final String key;

    public DimletKey(DimletType type, String key) {
        this.type = type;
        this.key = key;
    }

    public DimletKey(String serialized) {
        String[] split = serialized.split("#");
        this.type = DimletType.byName(split[0]);
        this.key = split[1];
    }

    public DimletType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String serialize() {
        return type.getShortName() + "#" + key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimletKey dimletKey = (DimletKey) o;
        return type == dimletKey.type &&
                Objects.equals(key, dimletKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key);
    }

    @Override
    public int compareTo(DimletKey dimletKey) {
        if (dimletKey.getType().equals(type)) {
            return key.compareTo(dimletKey.key);
        } else {
            return type.name().compareTo(dimletKey.type.name());
        }
    }
}
