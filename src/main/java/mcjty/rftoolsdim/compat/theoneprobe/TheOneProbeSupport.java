package mcjty.rftoolsdim.compat.theoneprobe;

import mcjty.lib.varia.Logging;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;

import javax.annotation.Nullable;

public class TheOneProbeSupport implements com.google.common.base.Function<ITheOneProbe, Void> {

    public static ITheOneProbe probe;

    public static int ELEMENT_DIMENSION;

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        Logging.log("Enabled support for The One Probe");
        ELEMENT_DIMENSION = probe.registerElementFactory(ElementDimension::new);
        return null;
    }

    public static IProbeInfo addDimensionElement(IProbeInfo probeInfo, int pct) {
        return probeInfo.element(new ElementDimension(pct));
    }
}