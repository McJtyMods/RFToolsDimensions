package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class SurfaceRuleDataBuilder {
    boolean what = false;
    boolean bedrockRoof = false;
    boolean bedrockFloor = false;

    public SurfaceRuleDataBuilder what(boolean what) {
        this.what = what;
        return this;
    }

    public SurfaceRuleDataBuilder bedrockRoof(boolean bedrockRoof) {
        this.bedrockRoof = bedrockRoof;
        return this;
    }

    public SurfaceRuleDataBuilder bedrockFloor(boolean bedrockFloor) {
        this.bedrockFloor = bedrockFloor;
        return this;
    }

    public static SurfaceRuleDataBuilder create() {
        return new SurfaceRuleDataBuilder();
    }

    public SurfaceRules.RuleSource build() {
        return SurfaceRuleData.overworldLike(what, bedrockRoof, bedrockFloor);
    }
}
