package mcjty.rftoolsdim.api.dimlet;

/**
 * Get a reference to an implementation of this interface by calling:
 *         FMLInterModComms.sendFunctionMessage("rftoolsdim", "getDimletConfigurationManager", "<whatever>.YourClass$GetDimletConfigurationManager");
 */
public interface IDimletConfigurationManager {

    /**
     * Create a builder for making a dimlet filter
     * @return
     */
    IFilterBuilder createFilterBuilder();

    /**
     * Create a builder for making a settings object
     * @return
     */
    ISettingsBuilder createSettingsBuilder();

    /**
     * Add a rule which will be executed before all other rules but after
     * all rules added by this mod before.
     * @param filter
     * @param settings
     */
    void addRule(IFilterBuilder filter, ISettingsBuilder settings);
}
