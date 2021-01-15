package mcjty.rftoolsdim.modules.dimlets.data;

import java.util.Objects;

public class DimletKey {

    private final DimletType type;
    private final String key;

    public DimletKey(DimletType type, String key) {
        this.type = type;
        this.key = key;
    }

    public DimletType getType() {
        return type;
    }

    public String getKey() {
        return key;
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
}
