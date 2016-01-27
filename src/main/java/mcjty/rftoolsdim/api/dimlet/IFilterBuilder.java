package mcjty.rftoolsdim.api.dimlet;

public interface IFilterBuilder {

    /**
     * Add a mod to the filter. If multiple mods are added then the filter
     * matches if any mod matches.
     * @param mod
     * @return
     */
    IFilterBuilder mod(String mod);

    /**
     * Add a dimlet name to the filter. If multiple names are added then the
     * filter matches if any name matches.
     *
     * This must correspond to:
     * * For material dimlets: the registry name of the block
     * * For liquid dimlets: the registry name of the liquid block
     * * For biomes: the name of the biome
     * * For mobs: a name as used by EntityList.stringToClassMapping
     * Wildcards (in the form of .*) are supported.
     * @param name
     * @return
     */
    IFilterBuilder name(String name);

    /**
     * Add a type to the filter. If multiple types are added then the
     * filter matches if any type matches.
     *
     * @param type A type like 'material', 'liquid', 'biome', 'mob', ...
     * @return
     */
    IFilterBuilder type(String type);

    /**
     * Add a blockstate property to the filter. This is only used for
     * material dimlets. If multiple properties are given then ALL
     * of them must match before the filter matches.
     *
     * @param name
     * @param value
     * @return
     */
    IFilterBuilder property(String name, String value);

    /**
     * Add a meta match to the filter. If multiple metas are added then
     * the filter matches if any meta matches.
     *
     * @param meta
     * @return
     */
    IFilterBuilder meta(Integer meta);
}
