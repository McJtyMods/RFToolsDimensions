package mcjty.rftoolsdim.items;

import mcjty.rftoolsdim.items.manual.RFToolsDimensionManualItem;
import mcjty.rftoolsdim.items.modules.DimensionModuleItem;
import mcjty.rftoolsdim.items.parts.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static DimletBaseItem dimletBaseItem;
    public static DimletControlCircuitItem dimletControlCircuitItem;
    public static DimletEnergyModuleItem dimletEnergyModuleItem;
    public static DimletMemoryUnitItem dimletMemoryUnitItem;
    public static DimletTypeControllerItem dimletTypeControllerItem;
    public static StructureEssenceItem structureEssenceItem;
    public static KnownDimlet knownDimletItem;
    public static EmptyDimensionTab emptyDimensionTabItem;
    public static RealizedDimensionTab realizedDimensionTabItem;
    public static DimletTemplate dimletTemplateItem;
    public static DimletParcelItem dimletParcelItem;

    public static RFToolsDimensionManualItem rfToolsDimensionManualItem;
    public static DimensionMonitorItem dimensionMonitorItem;
    public static DimensionModuleItem dimensionModuleItem;
    public static PhasedFieldGeneratorItem phasedFieldGeneratorItem;

    public static void init() {
        dimletBaseItem = new DimletBaseItem();
        dimletControlCircuitItem = new DimletControlCircuitItem();
        dimletEnergyModuleItem = new DimletEnergyModuleItem();
        dimletMemoryUnitItem = new DimletMemoryUnitItem();
        dimletTypeControllerItem = new DimletTypeControllerItem();
        structureEssenceItem = new StructureEssenceItem();
        knownDimletItem = new KnownDimlet();
        emptyDimensionTabItem = new EmptyDimensionTab();
        realizedDimensionTabItem = new RealizedDimensionTab();
        dimletTemplateItem = new DimletTemplate();
        dimletParcelItem = new DimletParcelItem();
        rfToolsDimensionManualItem = new RFToolsDimensionManualItem();
        dimensionMonitorItem = new DimensionMonitorItem();
        dimensionModuleItem = new DimensionModuleItem();
        phasedFieldGeneratorItem = new PhasedFieldGeneratorItem();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        dimletBaseItem.initModel();
        dimletControlCircuitItem.initModel();
        dimletEnergyModuleItem.initModel();
        dimletMemoryUnitItem.initModel();
        dimletTypeControllerItem.initModel();
        structureEssenceItem.initModel();
        knownDimletItem.initModel();
        emptyDimensionTabItem.initModel();
        realizedDimensionTabItem.initModel();
        dimletTemplateItem.initModel();
        dimletParcelItem.initModel();
        rfToolsDimensionManualItem.initModel();
        dimensionMonitorItem.initModel();
        dimensionModuleItem.initModel();
        phasedFieldGeneratorItem.initModel();
    }
}
