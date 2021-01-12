package mcjty.rftoolsdim.dimension.descriptor;

import mcjty.rftoolsdim.dimlets.DimletType;

public class DimletDescriptor {

    private final DimletType type;
    private final String name;

    public DimletDescriptor(DimletType type, String name) {
        this.type = type;
        this.name = name;
    }

    public DimletType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
