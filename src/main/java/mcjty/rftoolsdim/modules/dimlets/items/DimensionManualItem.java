package mcjty.rftoolsdim.modules.dimlets.items;

import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.Item;

public class DimensionManualItem extends Item implements ITooltipSettings {

    private final ManualEntry manualEntry;

    public DimensionManualItem(String entry) {
        super(Registration.createStandardProperties());
        this.manualEntry = ManualHelper.create(entry);
    }

    @Override
    public ManualEntry getManualEntry() {
        return manualEntry;
    }
}
