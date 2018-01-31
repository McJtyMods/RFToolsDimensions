package mcjty.rftoolsdim.dimensions.dimlets;

import java.util.Arrays;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;

/**
* Created by jorrit on 8/12/14.
*/
public class DimletKey implements Comparable<DimletKey> {
    private final DimletType type;
    private final String id;

    public DimletKey(DimletType type, String id) {
        this.type = type;
        this.id = id;
        if(id == null) {
            // StatList.reinit makes objects without proper NBT; ignore these...
            if(!Arrays.stream(Thread.currentThread().getStackTrace()).anyMatch(elem -> "net.minecraft.stats.StatList".equals(elem.getClassName()) && "reinit".equals(elem.getMethodName()))) {
                Logging.getLogger().catching(new NullPointerException("id"));
            }
        }
    }

    public DimletType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DimletKey dimletKey = (DimletKey) o;

        if (type != dimletKey.type) {
            return false;
        }
        if (id == null) {
            Logging.getLogger().catching(new NullPointerException("id"));
            if(dimletKey.id == null) {
                Logging.getLogger().catching(new NullPointerException("dimletKey.id"));
                return true;
            }
            return false;
        }
        if (!id.equals(dimletKey.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        if (id != null) {
            result = 31 * result + id.hashCode();
        } else {
            Logging.getLogger().catching(new NullPointerException("id"));
        }
        return result;
    }

    @Override
    public String toString() {
        return type.dimletType.getOpcode() + id;
    }

    public static DimletKey parseKey(String skey) {
        String opcode = skey.substring(0, 1);
        String name = skey.substring(1);
        return new DimletKey(DimletType.getTypeByOpcode(opcode), name);
    }

    @Override
    public int compareTo(DimletKey o) {
        int result = type.compareTo(o.type);
        if(result != 0) return result;
        if(id == null) {
            Logging.getLogger().catching(new NullPointerException("id"));
            if(o.id == null) {
                Logging.getLogger().catching(new NullPointerException("o.id"));
                return 0;
            }
            return -1;
        } else if(o.id == null) {
            Logging.getLogger().catching(new NullPointerException("o.id"));
            return 1;
        }
        return id.compareTo(o.id);
    }
}
