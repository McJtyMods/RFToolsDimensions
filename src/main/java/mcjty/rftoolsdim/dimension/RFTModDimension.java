package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;

import java.util.function.BiFunction;

public class RFTModDimension extends ModDimension {

    private final TerrainType terrainType;

    public RFTModDimension(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    @Override
    public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
        return (world, type) -> terrainType.getDimensionSupplier().apply(world, type);
    }
}
