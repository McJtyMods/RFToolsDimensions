package mcjty.rftoolsdim.apiimpl;

import mcjty.rftoolsdim.api.dimlet.IDimletConfigurationManager;
import mcjty.rftoolsdim.api.dimlet.IFilterBuilder;
import mcjty.rftoolsdim.api.dimlet.ISettingsBuilder;
import mcjty.rftoolsdim.config.Filter;
import mcjty.rftoolsdim.config.Settings;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimletConfigurationManager implements IDimletConfigurationManager {

    private final String mod;
    private final List<Pair<Filter, Settings>> rules = new ArrayList<>();

    private static final Map<String, DimletConfigurationManager> configurationManagers = new HashMap<>();

    public DimletConfigurationManager(String mod) {
        this.mod = mod;
        configurationManagers.put(mod, this);
    }

    public static Map<String, DimletConfigurationManager> getConfigurationManagers() {
        return configurationManagers;
    }

    public List<Pair<Filter, Settings>> getRules() {
        return rules;
    }

    @Override
    public void addRule(IFilterBuilder filterBuilder, ISettingsBuilder settingsBuilder) {
        Filter filter = ((Filter.Builder) filterBuilder).build();
        Settings settings = ((Settings.Builder) settingsBuilder).build();
        rules.add(Pair.of(filter, settings));
    }

    @Override
    public IFilterBuilder createFilterBuilder() {
        return new Filter.Builder();
    }

    @Override
    public ISettingsBuilder createSettingsBuilder() {
        return new Settings.Builder();
    }
}
