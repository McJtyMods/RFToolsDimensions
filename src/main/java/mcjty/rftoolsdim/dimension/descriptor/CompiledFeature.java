package mcjty.rftoolsdim.dimension.descriptor;

import mcjty.rftoolsdim.dimension.features.FeatureType;
import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CompiledFeature {

    private final FeatureType featureType;
    private final List<BlockState> blocks = new ArrayList<>();

    public CompiledFeature(FeatureType featureType) {
        this.featureType = featureType;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public List<BlockState> getBlocks() {
        return blocks;
    }
}
